package org.arboristasurbanos.treeplant

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.android.material.navigation.NavigationView
import org.arboristasurbanos.treeplant.database.DatabaseHandler
import org.arboristasurbanos.treeplant.databinding.ActivityMainBinding
import org.arboristasurbanos.treeplant.model.TreeModelClass
import org.arboristasurbanos.treeplant.ui.planting.PlantingFragment
import org.arboristasurbanos.treeplant.ui.settings.SettingsFragment
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val CHOOSE_FILE_REQUESTCODE = 8777
    private val PICKFILE_RESULT_CODE = 8778

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            /*
            R.id.action_contact -> {
                val mIntent = Intent(Intent.ACTION_SEND)
                mIntent.data = Uri.parse("mailto:")
                mIntent.type = "text/plain"
                mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("alexandrefelipemuller@gmail.com"))
                mIntent.putExtra(Intent.EXTRA_SUBJECT, "Planting a tree sugestion")
                mIntent.putExtra(Intent.EXTRA_TEXT, "Bug description:\nNew feature suggestion:")
                try {
                    //start email intent
                    startActivity(Intent.createChooser(mIntent, "Choose Email Client..."))
                }
                catch (e: Exception){
                    //if any thing goes wrong for example no email client application or any exception
                    //get and show exception message
                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                }
                true
            }
            */
            R.id.action_export -> {
                val databaseHandler: DatabaseHandler = DatabaseHandler(this)
                val trees: List<TreeModelClass> = databaseHandler.viewTrees()
                val HEADER = "Name, Date, Lat, Long\n"
                var filename = "export.csv"
                var path = this.getFileStreamPath("")
                var fileOut = File(path, filename)
                fileOut.delete()
                fileOut.createNewFile()
                fileOut.appendText(HEADER)
                for (tree in trees) {
                    fileOut.appendText("${tree.Name}, ${tree.Date}, ${tree.getLat()}, ${tree.getLong()}\n")
                }
                val sendIntent = Intent(Intent.ACTION_SEND)
                val uri = FileProvider.getUriForFile(
                    baseContext,
                    baseContext.applicationContext.packageName,
                    fileOut
                )
                sendIntent.putExtra(Intent.EXTRA_STREAM, uri)
                sendIntent.type = "text/csv"
                startActivity(Intent.createChooser(sendIntent, "SHARE"))
                true
            }
            R.id.action_import -> {
                val chooseFile = Intent(Intent.ACTION_GET_CONTENT)
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE)
                chooseFile.type = "text/csv"

                startActivityForResult(
                    Intent.createChooser(chooseFile, "Choose a file"),
                    PICKFILE_RESULT_CODE
                )
                true
            }
            R.id.actions_settings -> {
                var manager = this.getSupportFragmentManager()
                var transaction = manager?.beginTransaction()
                if (transaction != null) {
                    transaction.add(R.id.nav_host_fragment_content_main, SettingsFragment())
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == PICKFILE_RESULT_CODE){
            if (resultCode === Activity.RESULT_OK) {
                val content_describer: Uri = data!!.data!!
                try {
                    val stream: InputStream? =
                        getActivity(this)!!.getContentResolver().openInputStream(content_describer)
                    var reader: BufferedReader? = BufferedReader(InputStreamReader(stream))
                    var line: String?
                    if (reader != null) {
                        var header = reader.readLine()
                        if (!header.matches(Regex("Name, Date, Lat, Long"))){
                            Toast.makeText(
                                this, "Invalid file format",
                                Toast.LENGTH_LONG
                            ).show()
                            return
                        }
                        while (reader.readLine().also { line = it } != null) {
                            var newValues = line!!.split(',')
                            var newTree = TreeModelClass(Name = newValues[0],Date = newValues[1], Lat = newValues[2].toDouble(),Long = newValues[3].toDouble())
                            val databaseHandler: DatabaseHandler = DatabaseHandler(this)
                            databaseHandler.addTree(newTree)
                        }
                        Toast.makeText(
                            this, "Database imported with success",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}