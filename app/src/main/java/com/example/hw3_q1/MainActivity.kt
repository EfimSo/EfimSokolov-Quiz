package com.example.hw3_q1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.hw3_q1.ui.theme.HW3Q1Theme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HW3Q1Theme {

                val rememberScope = rememberCoroutineScope()
                val snackbarHostState = remember {
                    SnackbarHostState()
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(snackbarHostState)
                    },
                ) { innerPadding ->
                    Quiz(
                        modifier = Modifier.padding(innerPadding),
                        snackbarHost = snackbarHostState,
                        snackbarScope = rememberScope
                    )
                }
            }
        }
    }
}

@Composable
fun Quiz(modifier: Modifier = Modifier, snackbarHost: SnackbarHostState, snackbarScope: CoroutineScope) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var userInput by remember { mutableStateOf("") }
    var isQuizComplete by remember { mutableStateOf(false) }
    val firstPair = questions[0]
    var currentQuestion by remember { mutableStateOf(firstPair.first) }
    var currentAnswer by remember { mutableStateOf(firstPair.second) }

    val onUserInputChange: (String) -> Unit = {
        userInput = it
    }

    val changeQuestion: () -> Unit = {
        val updateState: (Int) -> Unit = { i ->
            val pair = questions[i]
            currentQuestion = pair.first
            currentAnswer = pair.second
        }

        if (currentQuestionIndex == questions.size - 1){
            snackbarScope.launch {
                val snackbarResult = snackbarHost.showSnackbar(message = "The answer is correct.\nYou have completed the quiz.",
                    actionLabel = "Restart", duration = SnackbarDuration.Long)
                if (snackbarResult == SnackbarResult.ActionPerformed) {
                    currentQuestionIndex = 0
                    updateState(currentQuestionIndex)
                }
            }
        }
        else{
            currentQuestionIndex++
            updateState(currentQuestionIndex)
        }
    }

    val onSubmitAnswer: () -> Unit = {
        val message: String
        val correct = (userInput.lowercase() == currentAnswer.lowercase())

        if (correct){
            message = "The answer is correct! Congratulations."
        }
        else{
            message = "The answer is incorrect. Please retry."
        }
        if (!correct || currentQuestionIndex != questions.size - 1) {
            snackbarScope.launch {
                snackbarHost.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
            }
        }
        if (correct) {
            changeQuestion()
        }
        userInput = ""
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
        ) {
            Column (horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = currentQuestion,
                    modifier = Modifier.padding(top = 15.dp, start = 15.dp, end = 15.dp),
                    fontSize = 20.sp
                )
                TextField(
                    value = userInput,
                    onValueChange = onUserInputChange,
                    label = { Text("Your Answer") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onSubmitAnswer,
                ) {
                    Text("Submit Answer")
                }
            }
        }
    }
    }


val questions = listOf(
    Pair(
        "Which animal spreads Lyme disease, a condition that affects many mammal species?",
        "Tick"
    ),
    Pair(
        "What is a white and black horse-like animal in Africa?",
        "Zebra"
    ),
    Pair(
        "What animal is large, gray and lives in both Africa and Asia?",
        "Elephant"
    ),
    Pair(
        "Which large cat is orange, has stripes and is sometimes found in Siberia?",
        "Tiger"
    )
)