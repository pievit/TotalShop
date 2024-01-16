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

class NotificationService(val appl: Application) : Service() {
    var NOTIFICATION_PERMISSION_ID: Int = 25
    private val repository: Repository = Repository(appl)
    val notificationManager: NotificationManager? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {appl.getSystemService(NotificationManager::class.java)} else null
    lateinit var notificationManagerCompat: NotificationManagerCompat
    var notifyOrdersList: MutableMap<Int,String> = mutableMapOf()

    companion object{
        const val CHANNEL_NAME = "OrdersChannel"
        const val CHANNEL_ID = "TotalShopNotificationServiceChannel"
        var pendingIntent: PendingIntent? = null
    }
    override fun onCreate() {
        super.onCreate()
        Log.d("Debug","Actual version -> "+Build.VERSION.SDK_INT.toString())
        Log.d("Debug","API 26 -> "+Build.VERSION_CODES.O.toString())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.enableVibration(true)
            notificationChannel.enableLights(true)

            notificationManager!!.createNotificationChannel(notificationChannel)
            Log.d("debug","Channel creato: "+notificationChannel.id)
        }else{
            notificationManagerCompat = NotificationManagerCompat.from(this)
        }


    }

    fun startNotificationService(){
        val timer = Timer()
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
        var sp = appl.getSharedPreferences("USER", Context.MODE_PRIVATE)
        if(sp.getString("USER_EMAIL",null).equals(mail)){
            val intent = Intent(appl.applicationContext, VenditoreActivity::class.java)
                intent.putExtra("notifOrder",id)
                intent.putExtra("email",mail)
            pendingIntent = PendingIntent.getActivity(appl.applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val notificationBuilder = NotificationCompat.Builder(appl.applicationContext, CHANNEL_ID)
                .setContentTitle("Hai un nuovo ordine!")
                .setContentText("Hai un nuovo ordine sul tuo store, vai a vederlo!")
                .setSmallIcon(R.drawable.ic_orders)
                .setContentIntent(pendingIntent)
                .build()
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                notificationManager!!.notify(1, notificationBuilder)
            }else{
                notificationManagerCompat.notify(1, notificationBuilder)
            }

        Log.d("Debug","Notification : user ->"+mail)
        }
    }


    @SuppressLint("MissingPermission")
    private fun sendNotificationConfirmed(mail: String,id :Int){
        var sp = appl.getSharedPreferences("USER", Context.MODE_PRIVATE)
        if(sp.getString("USER_EMAIL",null).equals(mail)) {
            val intent = Intent(appl.applicationContext, UtenteActivity::class.java)
            intent.putExtra("notifOrder",id)
            intent.putExtra("email",mail)
            pendingIntent = PendingIntent.getActivity(appl.applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val notificationBuilder = NotificationCompat.Builder(appl.applicationContext, CHANNEL_NAME)
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
        var sp = appl.getSharedPreferences("USER", Context.MODE_PRIVATE)
        if(sp.getString("USER_EMAIL",null).equals(mail)) {
            val intent = Intent(appl.applicationContext, VenditoreActivity::class.java)
            intent.putExtra("notifOrder",id)
            intent.putExtra("email",mail)
            pendingIntent = PendingIntent.getActivity(appl.applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val notificationBuilder = NotificationCompat.Builder(appl.applicationContext, CHANNEL_NAME)
                .setContentTitle("Il tuo ordine è stato cancellato!")
                .setContentText("Il tuo ordine è stato cancellato! Vai a vederlo sull'app!")
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
        var sp = appl.getSharedPreferences("USER", Context.MODE_PRIVATE)
        if (sp.getString("USER_EMAIL", null).equals(mail)) {
            val intent = Intent(appl.applicationContext, UtenteActivity::class.java)
            intent.putExtra("notifOrder",id)
            intent.putExtra("email",mail)
            pendingIntent = PendingIntent.getActivity(appl.applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            val notificationBuilder =
                NotificationCompat.Builder(appl.applicationContext, CHANNEL_NAME)
                    .setContentTitle("Il tuo ordine è stato annullato!")
                    .setContentText("Il tuo ordine è stato annullato! Vai a vederlo, sull'app!")
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
        stopSelf()
        notifyOrdersList.clear()
    }
}