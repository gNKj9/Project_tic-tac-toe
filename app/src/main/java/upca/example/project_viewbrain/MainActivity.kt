package upca.example.project_viewbrain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import upca.example.project_viewbrain.ui.theme.Project_ViewBrainTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project_ViewBrainTheme {
                MainApp()
            }
        }
    }
}

// Enum para controlar as janelas da App
enum class AppScreen {
    Start, Game
}

@Composable
fun MainApp() {
    var currentScreen by remember { mutableStateOf(AppScreen.Start) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Crossfade(
            targetState = currentScreen,
            label = "screen_transition",
            modifier = Modifier.padding(innerPadding)
        ) { screen ->
            when (screen) {
                AppScreen.Start -> StartScreen(onStartGame = { currentScreen = AppScreen.Game })
                AppScreen.Game -> GameScreen(onBack = { currentScreen = AppScreen.Start })
            }
        }
    }
}

@Composable
fun StartScreen(onStartGame: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        
        // TODO: SUBSTITUIR O LOGÓTIPO AQUI
        Image(
            painter = painterResource(id = R.drawable.galo),
            contentDescription = "Logótipo do Jogo",
            modifier = Modifier.size(160.dp) // Podes ajustar o tamanho aqui
        )

        Spacer(Modifier.height(48.dp))

        Text(
            text = "J O G O   D O   G A L O",
            fontSize = 24.sp,
            fontWeight = FontWeight.Light,
            letterSpacing = 2.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(16.dp))
        
        Text(
            text = "Desafia o teu cérebro",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(Modifier.height(80.dp))

        Button(
            onClick = onStartGame,
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp)
        ) {
            Text(
                text = "JOGAR",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun GameScreen(modifier: Modifier = Modifier, onBack: () -> Unit) {
    val brain = remember { GameBrain() }

    val message = when {
        brain.winner != null -> "VENCEU O JOGADOR ${brain.winner}"
        brain.isDraw -> "EMPATE"
        else -> "VEZ DO JOGADOR ${brain.currentPlayer}"
    }

    val statusColor = when {
        brain.winner == Player.X || (brain.winner == null && !brain.isDraw && brain.currentPlayer == Player.X) -> MaterialTheme.colorScheme.primary
        brain.winner == Player.O || (brain.winner == null && !brain.isDraw && brain.currentPlayer == Player.O) -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título e Botão Voltar minimalistas no topo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) {
                Text(
                    text = "← Voltar",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Normal
                )
            }
            Text(
                text = "J O G O",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
        }

        Spacer(Modifier.height(32.dp))

        // Placar limpo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ScoreCard("X", brain.scoreX, MaterialTheme.colorScheme.primary)
            ScoreCard("EMPATE", brain.draws, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
            ScoreCard("O", brain.scoreO, MaterialTheme.colorScheme.tertiary)
        }

        Spacer(Modifier.height(48.dp))

        // Tabuleiro 3x3 com grelha fina
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            for (row in 0..2) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    for (col in 0..2) {
                        val position = row * 3 + col
                        Cell(
                            value = brain.board[position],
                            highlight = position in brain.winningLine,
                            onClick = { brain.play(position) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(48.dp))

        // Mensagem de estado
        Crossfade(targetState = message, label = "message") { text ->
            Box(
                modifier = Modifier
                    .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(50))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = text,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center,
                    color = statusColor
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // Botões de controlo minimalistas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { brain.resetAll() },
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            ) {
                Text(
                    text = "REPOR PLACAR",
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { brain.newGame() },
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "NOVA PARTIDA",
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ScoreCard(title: String, value: Int, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            color = color,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "$value",
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun Cell(
    value: Player?,
    highlight: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (highlight) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
        else MaterialTheme.colorScheme.background,
        label = "background"
    )

    val scale by animateFloatAsState(
        targetValue = if (value != null) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val symbolColor = if (value == Player.X) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.tertiary

    Box(
        modifier = modifier
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value?.name ?: "",
            fontSize = 64.sp,
            fontWeight = FontWeight.Thin,
            color = symbolColor,
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainAppPreview() {
    Project_ViewBrainTheme {
        MainApp()
    }
}