package ipvc.estg.saveqr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import ipvc.estg.saveqr.api.ServiceBuilder
import ipvc.estg.saveqr.api.endpoints.usersEndpoint
import ipvc.estg.saveqr.api.models.Users
import ipvc.estg.saveqr.ui.listapasta.ListaPastaFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.

            //   val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            //       .requestEmail()
        //      .build()

        // Build a GoogleSignInClient with the options specified by gso.
        //    val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)




        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_listapasta), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val fraglog = intent.getStringExtra("EXTRA")
        if (fraglog == "ListarP") {
            val transition = supportFragmentManager.beginTransaction()
            val fragreg = ListaPastaFragment()
            transition.replace(R.id.drawer_layout, fragreg)
            transition.commit()
        }

        val request = ServiceBuilder.buildService(usersEndpoint::class.java)
        val call = request.getUsers()

        call.enqueue(object : Callback<Users> {
            override fun onResponse(call: Call<Users>, response: Response<Users>) {
                //Toast.makeText(applicationContext, response.body()!!.msg, Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<Users>, t: Throwable) {
                Toast.makeText(applicationContext, "DEU ERRO", Toast.LENGTH_LONG).show()
                Log.d("ENDPONT", t.toString())
            }
        })

    }




private fun <T> Call<T>.enqueue(callback: Callback<Users>) {
    TODO("Not yet implemented")
}

override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
