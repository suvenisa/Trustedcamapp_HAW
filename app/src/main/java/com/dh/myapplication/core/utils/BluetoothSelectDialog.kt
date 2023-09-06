package com.dh.myapplication.core.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties


@Composable
fun UserVerifiedDialog(showDialogScreen: Boolean, isVerified : Boolean , onClose: (Boolean) -> Unit) {

    var showDialog by remember { mutableStateOf(true) }

    if (showDialogScreen) {
        showDialog = false
    }





    Dialog(
        onDismissRequest = { showDialog = false },
        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
    ) {
        Card(
            modifier = Modifier
                .widthIn(360.dp, 480.dp)
                .height(180.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
            ,
            shape = RoundedCornerShape(12.dp)
        ) {

            Box(Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

                    // Top Session
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(start = 8.dp, end = 8.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {


                      val label =  if (isVerified) {
                            "Verified User"
                        } else {
                            "Unverified User"
                        }


                        Text( text = label, style = TextStyle(color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold), modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                        )



                    }


                    val subTitle  = if (isVerified) {
                        "All the Flash match , You are now a verified user."
                    } else {
                        // some flash is not match
                        "All the Flash match , You are now a verified user."
                    }



                    Box(Modifier.fillMaxWidth(), Alignment.Center) {
                        Text(text = subTitle, style = TextStyle(color = Color.Black, fontSize = 12.sp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))



                    Row(Modifier.fillMaxWidth() , horizontalArrangement = Arrangement.Center , verticalAlignment = Alignment.CenterVertically) {

                        Button(onClick = {
                            onClose(true)

                        }) {
                            Text(text = "Show logs")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(onClick = {
                            onClose(false)

                        }) {
                            Text(text = "Close")
                        }

                    }

                }

            }
        }

    }

}
