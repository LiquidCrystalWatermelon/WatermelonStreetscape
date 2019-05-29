package com.kotlinproject.wooooo.watermelonstreetscape.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceActivity
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListView
import com.kotlinproject.wooooo.watermelonstreetscape.R
import com.kotlinproject.wooooo.watermelonstreetscape.utils.FileUtils
import com.kotlinproject.wooooo.watermelonstreetscape.utils.spServiceIp
import kotlinx.android.synthetic.main.include_app_bar.view.*
import java.io.File

class SettingsActivity : PreferenceActivity() {
    private var clear = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.setting_main)
        val prefClear = findPreference("data_clear")
        prefClear.setOnPreferenceClickListener {
            AlertDialog
                .Builder(this)
                .setTitle("数据清除后将无法恢复，确定要清除数据？")
                .setPositiveButton("确定") { _, _ ->
                    File(FileUtils.imageDictPath).apply { if (exists()) deleteRecursively() }
                    File(FileUtils.scapeDictPath).apply { if (exists()) deleteRecursively() }
                    clear = true
                }
                .setNegativeButton("取消", null)
                .show()
            true
        }
        val prefIp = findPreference("service_ip")
        prefIp.title = spServiceIp
        prefIp.setOnPreferenceChangeListener { _, newValue ->
            prefIp.title = newValue.toString()
            true
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        val root = findViewById<ListView>(android.R.id.list).parent.parent.parent
        val bar = LayoutInflater
            .from(this)
            .inflate(R.layout.include_app_bar, root as ViewGroup, false)
            as AppBarLayout
        root.addView(bar, 0)
        with(bar.toolbar_app) {
            title = "设置"
            navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_black_24dp)
            setNavigationOnClickListener { finish() }
        }
    }

    override fun finish() {
        val intent = Intent().putExtra(MainActivity.EXTRA_CLEAR_SCAPE, clear)
        setResult(Activity.RESULT_OK, intent)
        super.finish()
    }
}
