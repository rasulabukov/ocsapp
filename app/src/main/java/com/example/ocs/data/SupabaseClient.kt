package com.example.ocs.data

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

object SupabaseClient {

    val supabase = createSupabaseClient(
        supabaseUrl = "https://lljlbfavftgwlycvrkqr.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImxsamxiZmF2ZnRnd2x5Y3Zya3FyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzE4ODE0MjIsImV4cCI6MjA0NzQ1NzQyMn0.DpMSfrnpVn3VYR8GWAnW0DxYoM3VoY-AKOv2-GGNyus"
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
        install(Realtime)

    }
}