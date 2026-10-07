package upca.example.project_viewbrain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// Os dois jogadores possíveis
enum class Player { X, O }

// BRAIN: guarda o estado do jogo e aplica as regras.
// Não sabe nada sobre o ecrã.
class GameBrain {

    // Todas as combinações que dão vitória (índices do tabuleiro 0 a 8)
    private val winningLines = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // linhas
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // colunas
        listOf(0, 4, 8), listOf(2, 4, 6)                   // diagonais
    )

    // Tabuleiro com 9 posições: null = vazia, senão X ou O
    var board by mutableStateOf<List<Player?>>(List(9) { null })
        private set

    // Jogador a quem pertence a vez
    var currentPlayer by mutableStateOf(Player.X)
        private set

    // Vencedor da partida (null se ainda não há)
    var winner by mutableStateOf<Player?>(null)
        private set

    // Posições da linha vencedora, usadas para destacar as células
    var winningLine by mutableStateOf<List<Int>>(emptyList())
        private set

    // Indica se a partida terminou empatada
    var isDraw by mutableStateOf(false)
        private set

    // Placar acumulado
    var scoreX by mutableIntStateOf(0)
        private set

    var scoreO by mutableIntStateOf(0)
        private set

    var draws by mutableIntStateOf(0)
        private set

    // A partida acabou se houver vencedor ou empate
    val isFinished: Boolean
        get() = winner != null || isDraw

    // Executa uma jogada na posição indicada
    fun play(position: Int) {
        // Ignora se a partida acabou ou se a célula já está ocupada
        if (isFinished || board[position] != null) return

        // Cria uma nova lista com a jogada (o Compose deteta a mudança)
        board = board.toMutableList().also { it[position] = currentPlayer }

        // Procura uma linha completa do jogador atual
        val line = winningLines.firstOrNull { l -> l.all { board[it] == currentPlayer } }

        when {
            // Vitória: guarda o vencedor e soma ponto
            line != null -> {
                winner = currentPlayer
                winningLine = line
                if (currentPlayer == Player.X) scoreX++ else scoreO++
            }
            // Tabuleiro cheio sem vencedor: empate
            board.all { it != null } -> {
                isDraw = true
                draws++
            }
            // Caso contrário, passa a vez ao outro jogador
            else -> currentPlayer = if (currentPlayer == Player.X) Player.O else Player.X
        }
    }

    // Limpa o tabuleiro mas mantém o placar
    fun newGame() {
        board = List(9) { null }
        currentPlayer = Player.X
        winner = null
        winningLine = emptyList()
        isDraw = false
    }

    // Limpa o tabuleiro e também o placar
    fun resetAll() {
        newGame()
        scoreX = 0
        scoreO = 0
        draws = 0
    }
}