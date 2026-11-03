package com.example.access_tracker.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.access_tracker.R
import com.example.access_tracker.ui.theme.TintaBase

/** Iconos de Fundamentos → Iconos (Figma). */
enum class Ico(@DrawableRes val res: Int) {
    Home(R.drawable.ic_home),
    Users(R.drawable.ic_users),
    Calendar(R.drawable.ic_calendar),
    Idcard(R.drawable.ic_idcard),
    Inbox(R.drawable.ic_inbox),
    Bell(R.drawable.ic_bell),
    More(R.drawable.ic_more),
    Fingerprint(R.drawable.ic_fingerprint),
    Calcheck(R.drawable.ic_calcheck),
    File(R.drawable.ic_file),
    Flag(R.drawable.ic_flag),
    Report(R.drawable.ic_report),
    Building(R.drawable.ic_building),
    Chart(R.drawable.ic_chart),
    History(R.drawable.ic_history),
    Plus(R.drawable.ic_plus),
    Check(R.drawable.ic_check),
    X(R.drawable.ic_x),
    Back(R.drawable.ic_back),
    Chevron(R.drawable.ic_chevron),
    Chevdown(R.drawable.ic_chevdown),
    Login(R.drawable.ic_login),
    Logout(R.drawable.ic_logout),
    Pin(R.drawable.ic_pin),
    Wifioff(R.drawable.ic_wifioff),
    Cloud(R.drawable.ic_cloud),
    Alert(R.drawable.ic_alert),
    Search(R.drawable.ic_search),
    Lock(R.drawable.ic_lock),
    Eye(R.drawable.ic_eye),
    Megaphone(R.drawable.ic_megaphone),
    Clock(R.drawable.ic_clock),
    Download(R.drawable.ic_download),
    Settings(R.drawable.ic_settings),
    Shield(R.drawable.ic_shield),
    Userplus(R.drawable.ic_userplus),
    Userminus(R.drawable.ic_userminus),
    Copy(R.drawable.ic_copy),
    Send(R.drawable.ic_send),
    Edit(R.drawable.ic_edit),
    Info(R.drawable.ic_info),
    Swap(R.drawable.ic_swap),
    Refresh(R.drawable.ic_refresh),
    Sun(R.drawable.ic_sun),
    Moon(R.drawable.ic_moon),
    Coffee(R.drawable.ic_coffee),
}

@Composable
fun Icono(
    icono: Ico,
    modifier: Modifier = Modifier,
    tint: Color = TintaBase,
    tamano: Dp = 24.dp,
) {
    Icon(
        painter = painterResource(icono.res),
        contentDescription = null,
        tint = tint,
        modifier = modifier.size(tamano),
    )
}
