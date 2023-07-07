package main

case class Location( x: Int,y: Int) {
  def mooreNeighborhood(world: World): List[Location] = {
    val xCoords = List(x - 1, x, x + 1).filter(x => x >= 0 && x < world.length)
    val yCoords = List(y - 1, y, y + 1).filter(y => y >= 0 && y < world.length)
    val coords = for {
      x <- xCoords
      y <- yCoords
      if !(x == this.x && y == this.y)
    } yield Location(x, y)
    coords
  }

}
