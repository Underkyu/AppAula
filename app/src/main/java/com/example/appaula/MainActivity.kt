package com.example.appaula


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.appaula.roomDB.Pessoa
import com.example.appaula.ui.theme.AppAulaTheme
import com.example.appaula.roomDB.PessoaDataBase
import com.example.appaula.viewModel.PessoaViewModel
import com.example.appaula.viewModel.Repository

class MainActivity : ComponentActivity() {
    private val db by lazy{
        Room.databaseBuilder(
            applicationContext,
            PessoaDataBase::class.java,
            "pessoa.db"
        ).build()
    }

    private val viewModel by viewModels<PessoaViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return PessoaViewModel(Repository(db)) as T                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppAulaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Cadastro(viewModel,this)
                }
            }
        }
    }
}


@Composable
fun Cadastro(viewModel: PessoaViewModel, mainActivity: MainActivity){

    var nome by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }

    val pessoa = Pessoa(
        nome,
        telefone
    )

    var pessoaList by remember{ mutableStateOf(listOf<Pessoa>()) }

    viewModel.getPessoa().observe(mainActivity){
        pessoaList = it
    }

    Column (modifier = Modifier.background(color = Color(67,159,230,90))) {
        Row {
            Spacer(modifier = Modifier.height(20.dp))
        }

        Row (horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text(text = "App Database", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 30.sp, color = Color(27,47,255,175))
        }

        Spacer(modifier = Modifier.height(20.dp))
        Row (horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            TextField(value = nome, onValueChange = {nome = it}, label = { Text("Nome") })
        }

        Spacer(modifier = Modifier.height(20.dp))
        Row (horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            TextField(value = telefone, onValueChange = {telefone = it}, label = { Text("Telefone") })
        }

        Spacer(modifier = Modifier.height(20.dp))
        Row (horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                viewModel.upsertPessoa(pessoa)
                nome = ""
                telefone = ""
            }, colors = ButtonDefaults.buttonColors(containerColor = Color(27,47,255,200))) {
                Text(text = "Cadastrar")
            }
        }
        Divider()
        LazyColumn {
            items(pessoaList){ pessoa->
                Row(
                    Modifier.fillMaxWidth(),
                    Arrangement.Center
                ) {
                    Column(
                        Modifier.fillMaxWidth(0.5f),
                        Arrangement.Center
                    ) {
                        Text(text = "${pessoa.nome}")
                    }

                    Column(
                        Modifier.fillMaxWidth(0.5f),
                        Arrangement.Center
                    ) {
                        Text(text = "${pessoa.telefone}")
                    }
                }
                Divider()
            }
        }
    }
}

