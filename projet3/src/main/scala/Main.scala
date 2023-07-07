import scalafx.Includes._
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.util.Duration
import scalafx.scene.input.{KeyCode, KeyEvent}
import scala.math.sqrt
import scala.util.Random

object Main extends JFXApp3 {
  val canvasWidth = 600
  val canvasHeight = 400
  val racketWidth = 20
  val racketHeight = 80
  val ballRadius = 10

  var racketY: Double = canvasHeight / 2 - racketHeight / 2
  var ballX: Double = canvasWidth / 2
  var ballY: Double = canvasHeight / 2
  var ballSpeedX: Double = 2
  var ballSpeedY: Double = 2

  val racket = new Rectangle {
    width = racketWidth
    height = racketHeight
    x = canvasWidth - racketWidth - 30
    y = racketY
    fill = Color.White
  }

  val ball = new Circle {
    centerX = ballX
    centerY = ballY
    radius = ballRadius
    fill = Color.White
  }

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Ping Pong Game"
      width = canvasWidth
      height = canvasHeight

      scene = new Scene {
        fill = Color.Black
        content = List(racket, ball)

        onKeyPressed = (event: KeyEvent) => {
          event.code match {
            case KeyCode.Up =>
              moveRacket(-20)
            case KeyCode.Down =>
              moveRacket(20)
            case _ =>
          }
        }

        val timeline = new Timeline {
          keyFrames = Seq(
            KeyFrame(Duration(10), onFinished = () => {
              updateBall()
            })
          )
          cycleCount = Timeline.Indefinite
        }
        timeline.play()
      }
    }
  }

  def moveRacket(deltaY: Double): Unit = {
    racketY += deltaY
    if (racketY < 0) racketY = 0
    if (racketY > canvasHeight - racketHeight) racketY = canvasHeight - racketHeight
    racket.y = racketY
  }

  def updateBall(): Unit = {
    ballX += ballSpeedX
    ballY += ballSpeedY

    if (ballX <= 0 || ballX >= canvasWidth) {
      ballSpeedX = -ballSpeedX
    }
    if (ballY <= 0 || ballY >= canvasHeight) {
      ballSpeedY = -ballSpeedY
    }

    if (ball.intersects(racket.boundsInParent())) {
      ballSpeedX = -ballSpeedX
    }

    ball.centerX = ballX
    ball.centerY = ballY
  }


}
