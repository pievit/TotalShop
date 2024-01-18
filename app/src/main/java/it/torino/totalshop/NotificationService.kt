package it.torino.totalshop

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import it.torino.totalshop.roomdb.Repository
import it.torino.totalshop.utente.UtenteActivity
import it.torino.totalshop.venditore.VenditoreActivity
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class NotificationService() : Service() {
    private lateinit var repository: Repository
    lateinit var notificationManager: NotificationManager
    lateinit var notificationManagerCompat: NotificationManagerCompat
    var notifyOrdersList: MutableMap<Int,String> = mutableMapOf()
    lateinit var scheduler : ScheduledExecutorService
    lateinit var spNotOrd : SharedPreferences
    companion object{
        const val NOTIFICATION_PERMISSION_ID: Int = 25
        const val CHANNEL_NAME = "OrdersChannel"
        const val CHANNEL_ID = "TotalShopNotificationServiceChannel"
        var pendingIntent: PendingIntent? = null
    }
    override fun onCreate() {
        super.onCreate()
        spNotOrd = application.getSharedPreferences("NOTIFY", Context.MODE_PRIVATE)
        scheduler = Executors.newScheduledThreadPool(1)
        repository = Repository(application)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager = application.getSystemService(NotificationManager::class.java)
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.enableVibration(true)
            notificationChannel.enableLights(true)

            notificationManager.createNotificationChannel(notificationChannel)
            Log.d("NotificationLog","Channel creato: "+notificationChannel.id)
        }else{
            notificationManagerCompat = NotificationManagerCompat.from(applicationContext)
        }





    }

    fun stopNotificationService(){
        scheduler.shutdown()
        Log.d("NotificationLog","Stop Servizio")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scheduler.shutdown()
        startNotificationService()
        return START_STICKY
    }


    fun startNotificationService(){
        val initialDelay = 0L
        val period = 30L
        scheduler = Executors.newScheduledThreadPool(1)
        scheduler.scheduleAtFixedRate({
            Log.d("NotificationLog","Inizio servizio")
            var ordersList = repository.dbOrdersDataDAO?.getAllOrders()
            if(ordersList!=null){
                for(ord in ordersList){
                    var vendor = repository.dbStoreDataDAO?.getOwner(ord.storeId)
                    if(!ord.status.equals("nuovo")){
                        if(spNotOrd.contains("Order"+ord.id.toString())){
                            when(ord.status){
                                "Confermato"-> sendNotificationConfirmed(ord.usermail,ord.id)
                                "Annullato" -> sendNotificationAnnulled(ord.usermail,ord.id)
                                "Cancellato" -> sendNotificationCanceled(vendor!!.email,ord.id)
                            }
                            spNotOrd.edit().remove("Order"+ord.id.toString()).apply()
                        }


                    }else{
                        Log.d("Debug",spNotOrd.all.toString())
                        if(!spNotOrd.contains("Order"+ord.id.toString())){
                            spNotOrd.edit().putInt("Order"+ord.id.toString(),ord.id).apply()
                            sendNotificationNewOrder(vendor!!.email,ord.id)
                        }
                    }
                }
            }
        }, initialDelay, period, TimeUnit.SECONDS)
    }


    @SuppressLint("MissingPermission")
    private fun sendNotificationNewOrder(mail: String,id :Int){
        val sp = application.getSharedPreferences("USER", Context.MODE_PRIVATE)
        if(sp.getString("USER_EMAIL",null).equals(mail)){
            val intent = Intent(applicationContext, VenditoreActivity::class.java)
            intent.putExtra("notifOrder",id)
            intent.putExtra("email",mail)
            intent.putExtra("userType",true)
            pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setContentTitle("Hai un nuovo ordine!")
                .setContentText("Hai un nuovo ordine sul tuo store, vai a vederlo!")
                .setSmallIcon(R.drawable.ic_orders)
                .setContentIntent(pendingIntent)
                .build()
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                notificationManager.notify(1, notificationBuilder)
            }else{
                notificationManagerCompat.notify(1, notificationBuilder)
            }

        Log.d("NotificationLog","Notification : user ->"+mail)
        }
    }


    @SuppressLint("MissingPermission")
    private fun sendNotificationConfirmed(mail: String,id :Int){
        var sp = application.getSharedPreferences("USER", Context.MODE_PRIVATE)
        if(sp.getString("USER_EMAIL",null).equals(mail)) {
            val intent = Intent(applicationContext, UtenteActivity::class.java)
            intent.putExtra("notifOrder",id)
            intent.putExtra("email",mail)
            intent.putExtra("userType",false)
            pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_NAME)
                .setContentTitle("Il tuo ordine è stato confermato!")
                .setContentText("Il tuo ordine è stato confermato dal venditore! Vai a vederlo sull'app!")
                .setSmallIcon(R.drawable.ic_orders)
                .setChannelId(CHANNEL_ID.toString())
                .setContentIntent(pendingIntent)
                .build()
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                notificationManager!!.notify(1, notificationBuilder)
            }else{
                notificationManagerCompat.notify(1, notificationBuilder)
            }
        }
        Log.d("NotificationLog","Notification : user ->"+mail)
    }

    @SuppressLint("MissingPermission")
    private fun sendNotificationCanceled(mail: String,id :Int){
        var sp = application.getSharedPreferences("USER", Context.MODE_PRIVATE)
        if(sp.getString("USER_EMAIL",null).equals(mail)) {
            val intent = Intent(applicationContext, VenditoreActivity::class.java)
            intent.putExtra("notifOrder",id)
            intent.putExtra("email",mail)
            intent.putExtra("userType",true)
            pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_NAME)
                .setContentTitle("Un Utente ha cancellato il suo ordine!")
                .setContentText("Un ordine è stato cancellato dall'Utente! Vai a vederlo sull'app!")
                .setSmallIcon(R.drawable.ic_orders)
                .setChannelId(CHANNEL_ID.toString())
                .setContentIntent(pendingIntent)
                .build()
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                notificationManager!!.notify(1, notificationBuilder)
            }else{
                notificationManagerCompat.notify(1, notificationBuilder)
            }
        }
        Log.d("NotificationLog","Notification : user ->"+mail)
    }


    @SuppressLint("MissingPermission")
    private fun sendNotificationAnnulled(mail: String,id :Int) {
        var sp = application.getSharedPreferences("USER", Context.MODE_PRIVATE)
        if (sp.getString("USER_EMAIL", null).equals(mail)) {
            val intent = Intent(applicationContext, UtenteActivity::class.java)
            intent.putExtra("notifOrder",id)
            intent.putExtra("email",mail)
            intent.putExtra("userType",false)
            pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val notificationBuilder =
                NotificationCompat.Builder(applicationContext, CHANNEL_NAME)
                    .setContentTitle("Il tuo ordine è stato annullato!")
                    .setContentText("Il tuo ordine è stato annullato dal venditore! Vai a vederlo, sull'app!")
                    .setSmallIcon(R.drawable.ic_orders)
                    .setChannelId(CHANNEL_ID.toString())
                    .setContentIntent(pendingIntent)
                    .build()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager!!.notify(1, notificationBuilder)
            } else {
                notificationManagerCompat.notify(1, notificationBuilder)
            }
            Log.d("NotificationLog", "Notification : user ->" + mail)
        }
    }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        val app = spNotOrd.getBoolean("NOTIFICATIONS",false)
        spNotOrd.edit().clear().apply()
        spNotOrd.edit().putBoolean("NOTIFICATIONS",app).apply()
        stopNotificationService()
    }
}