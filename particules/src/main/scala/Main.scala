import com.sun.javafx.scene.traversal.Direction
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.animation.Timeline
import scalafx.animation.KeyFrame
import scalafx.util.Duration

import concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random
import scalafx.Includes.eventClosureWrapperWithZeroParam
import scalafx.event.EventIncludes.eventClosureWrapperWithZeroParam

import scala.math.sqrt



object Main extends JFXApp3 {
  type Particule = (Int, Int, Int, Direction)
  type Direction =(Int, Int)
  def initialParticule: Particule = {
    val pas1: Int = Random.nextInt(21) - 10
    val pas2: Int = Random.nextInt(21) - 10
    (Random.nextInt(600), Random.nextInt(600), 4, (pas1, pas2))
  }


  def createParticules: List[Particule] = {
    val nombreParticules = 150
    //val nombreParticules = Random.nextInt(200)
    List.fill(nombreParticules)(initialParticule)
  }

  val particules: List[Circle] = drawParticules()


  def drawParticules(): List[Circle] = {
    val particules = createParticules.map { particule =>
      val circle = new Circle {
        centerX = particule._1
        centerY = particule._2
        radius = particule._3
        fill = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
      }
      circle.userData = particule._4
      circle
    }
    particules
  }
  def updateParticules(particules: List[Circle]): Unit = {
    particules.zipWithIndex.foreach { case (particule, index) =>
      val direction = particule.userData.asInstanceOf[Direction]
      moveParticule(particule, direction)

      val collidedParticules = particules.zipWithIndex.filter { case (p, i) =>
        i != index && hasCollisionWith(particule, p)
      }
      collidedParticules.foreach { case (collidedParticule, _) =>
        val newDirection1 = calculateNewDirection(particule.userData.asInstanceOf[Direction])
        val newDirection2 = calculateNewDirection(collidedParticule.userData.asInstanceOf[Direction])

        particule.userData = newDirection1
        collidedParticule.userData = newDirection2
      }

      if (hasCollisionCanvas(particule)) {
        val currentDirection = particule.userData.asInstanceOf[Direction]
        val newDirection = (-currentDirection._1, -currentDirection._2)
        particule.userData = newDirection
      }
    }
  }


  def calculateNewDirection(direction: Direction): Direction = {
    val newDirection = (-direction._1, -direction._2)
    val randomOffset = (Random.nextInt(3) - 1, Random.nextInt(3) - 1) // Ajouter une petite composante aléatoire
    (newDirection._1 + randomOffset._1, newDirection._2 + randomOffset._2)
  }

  def hasCollisionCanvas(particule: Circle): Boolean = {
    val radius = particule.radius()
    val canvasWidth = 600
    val canvasHeight = 600

    val centerX = particule.centerX()
    val centerY = particule.centerY()

    val collidedLeft = centerX - radius <= 0
    val collidedRight = centerX + radius >= canvasWidth
    val collidedTop = centerY - radius <= 0
    val collidedBottom = centerY + radius >= canvasHeight

    collidedLeft || collidedRight || collidedTop || collidedBottom
  }

  def hasCollisionWith(particule1: Circle, particule2: Circle): Boolean = {
    val distance = sqrt(
      (particule1.centerX() - particule2.centerX()) * (particule1.centerX() - particule2.centerX()) +
        (particule1.centerY() - particule2.centerY()) * (particule1.centerY() - particule2.centerY())
    )
    distance <= (particule1.radius() + particule2.radius())
  }

  def moveParticule(particule: Circle, direction: Direction): Unit = {
    val speed = 2

    val magnitude = sqrt(direction._1 * direction._1 + direction._2 * direction._2)
    val normalizedDirection = (direction._1 / magnitude, direction._2 / magnitude)

    val newX = particule.centerX() + normalizedDirection._1 * speed
    val newY = particule.centerY() + normalizedDirection._2 * speed

    particule.centerX() = newX % 600
    particule.centerY() = newY % 600
  }

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "Colored Window"
      width = 600
      height = 600
      scene = new Scene {
        fill = Color.rgb(8, 17, 92)
        content = particules
      }

      val timeline = new Timeline {
        keyFrames = Seq(
          KeyFrame(Duration(15), onFinished = () => {
            updateParticules(particules)
          })
        )
        cycleCount = Timeline.Indefinite
      }
      timeline.play()
    }
  }

}

