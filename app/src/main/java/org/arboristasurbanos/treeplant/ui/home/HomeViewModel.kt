package org.arboristasurbanos.treeplant.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.arboristasurbanos.treeplant.R
import org.arboristasurbanos.treeplant.database.DatabaseHandler
import org.arboristasurbanos.treeplant.model.TreeModelClass
import java.text.SimpleDateFormat
import java.util.*


class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _text = MutableLiveData<String>().apply {
        value = loadTrees()
    }

    private fun loadTrees(): String? {
        val context = getApplication<Application>().applicationContext
        val databaseHandler: DatabaseHandler = DatabaseHandler(context)
        val trees: List<TreeModelClass> = databaseHandler.viewTrees()
        val num = trees.count()

        val sdf = SimpleDateFormat("dd/MM/yyyy")
        var oldest = Date()
        var lastMonthCount = 0
        var lastMonthDate = Date(Date().time - 2505600000)
        for (tree in trees){
            if (sdf.parse(tree.Date).before(oldest))
                oldest = sdf.parse(tree.Date)
            if (sdf.parse(tree.Date).after(lastMonthDate))
                lastMonthCount++
        }
        if (num > 0) {
            return context.getString(R.string.home_planted1, num, friendlyDate(Date().time-oldest.time),lastMonthCount)
            //X last week
            //X last month
            //Oldest tree
        }
        else
            return context.getString(R.string.home_planted_no_tree)
    }
    private fun friendlyDate(input: Long):String?{
        val context = getApplication<Application>().applicationContext
        var days = (input/86400000)
        if (days > 360)
            return (days).floorDiv(360).toString() + " " + context.getString(R.string.home_years)
        else
            return days.toString() + " " + context.getString(R.string.home_days)
    }
    val text: LiveData<String> = _text
}