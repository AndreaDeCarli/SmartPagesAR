package com.example.smartpagesar

import android.app.Application
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SmartPagesARApplication: Application() {
    lateinit var supabase: SupabaseClient

    override fun onCreate() {
        super.onCreate()

        supabase = createSupabaseClient(
            supabaseUrl = "https://krsdggggvjyavipdgvwp.supabase.co",
            supabaseKey = "sb_publishable_QgEe0EywkRl7gxGm8dwUFA_lqUujfLk"
        ) {
            install(Auth)
            install(Storage)
            install(Postgrest)
        }

        startKoin {
            androidLogger()
            androidContext(this@SmartPagesARApplication)
            modules(appModule)
        }
    }
}