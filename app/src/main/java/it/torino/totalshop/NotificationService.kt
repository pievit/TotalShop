package it.torino.totalshop

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import it.torino.totalshop.login.LoginActivity
import it.torino.totalshop.roomdb.Repository
import it.torino.totalshop.utente.UtenteActivity
import it.torino.totalshop.venditore.VenditoreActivity
import java.util.Timer
import java.util.TimerTask

class NotificationService() : Service() {
    private lateinit var repository: Repository
    lateinit var notificationManager: NotificationManager
    lateinit var notificationManagerCompat: NotificationManagerCompat
    var notifyOrdersList: MutableMap<Int,String> = mutableMapOf()
    val timer = Timer()
    var flagtimer = false


    companion object{
        const val NOTIFICATION_PERMISSION_ID: Int = 25
        const val CHANNEL_NAME = "OrdersChannel"
        const val CHANNEL_ID = "TotalShopNotificationServiceChannel"
        var pendingIntent: PendingIntent? = null
    }
    override fun onCreate() {
        super.onCreate()
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
            Log.d("debug","Channel creato: "+notificationChannel.id)
        }else{
            notificationManagerCompat = NotificationManagerCompat.from(applicationContext)
        }



    }

    fun stopNotificationService(){
        timer.cancel()
        Log.d("Debug","Stop Servizio")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(flagtimer)
            timer.cancel()
        else
            flagtimer=true
        startNotificationService()
        return START_STICKY
    }


    fun startNotificationService(){
        timer.schedule(object : TimerTask() {
            override fun run() {
                Log.d("Debug","Inizio service")
                var ordersList = repository.dbOrdersDataDAO?.getAllOrders()
                if(ordersList!=null){
                    for(ord in ordersList){
                        var vendor = repository.dbStoreDataDAO?.getOwner(ord.storeId)
                        if(!ord.status.equals("nuovo")){
                            if(notifyOrdersList.contains(ord.id) && notifyOrdersList.get(ord.id).equals("nuovo")){
                                Log.d("Debug",ord.status)
                                when(ord.status){
                                   "Confermato"-> sendNotificationConfirmed(ord.usermail,ord.id)
                                    "Annullato" -> sendNotificationAnnulled(ord.usermail,ord.id)
                                    "Cancellato" -> sendNotificationCanceled(vendor!!.email,ord.id)
                                }

                                notifyOrdersList.remove(ord.id)
                            }


                        }else{
                            if(!notifyOrdersList.contains(ord.id)){
                                Log.d("Debug","Inserito new prod")
                                notifyOrdersList.put(ord.id,ord.status)
                                sendNotificationNewOrder(vendor!!.email,ord.id)
                            }
                        }
                    }
                }
            }
        }, 0, 30000)
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

        Log.d("Debug","Notification : user ->"+mail)
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
        Log.d("Debug","Notification : user ->"+mail)
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
        Log.d("Debug","Notification : user ->"+mail)
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
            Log.d("Debug", "Notification : user ->" + mail)
        }
    }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopNotificationService()
        flagtimer = false
        notifyOrdersList.clear()
    }
}