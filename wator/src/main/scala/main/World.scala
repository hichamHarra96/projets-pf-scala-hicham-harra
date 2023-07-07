package main

case class World(length: Int = 50, nSharks: Int = 10, nTuna: Int = 10, var sharksList: List[(Shark, Location)], var tunaList: List[(Tuna, Location)], var occupiedLocations: List[Location] = List(), var tBreed: Int = 7, var sBreed: Int = 5, var sEnergy: Int = 7) {



  if (nSharks + nTuna > length * length) {
    throw new IllegalArgumentException("Too many fish for the size of the world")
  }

  def isLocationEmpty(location: Location): Boolean = {
    !occupiedLocations.contains(location)
  }

  def randomCoord: Int = {
    scala.util.Random.nextInt(length)
  }

  def randomEmptyLocation: Location = {
    val location = Location(randomCoord, randomCoord)
    if (isLocationEmpty(location)) {
      this.occupiedLocations = location :: occupiedLocations
      location
    } else {
      randomEmptyLocation
    }
  }

  //find if a location is occupied by a tuna
  def isLocationOccupiedByTuna(location: Location): Boolean = {
    tunaList.exists(_._2 == location)
  }

  //move a tuna to case in moore neighborhood
  def moveTuna(tuna: Tuna, location: Location): Unit = {
    //choose a random new location from the moore neighborhood if it is empty if not do nothing starting with those occupied by tuna
    val mooreNeighborhood = location.mooreNeighborhood(this)
    val emptyMooreNeighborhood = mooreNeighborhood.filter(isLocationEmpty)
    if (emptyMooreNeighborhood.nonEmpty) {
      val newLocation = emptyMooreNeighborhood(scala.util.Random.nextInt(emptyMooreNeighborhood.length))
      this.tunaList = (tuna, newLocation) :: tunaList.filterNot(_._2 == location)
      this.occupiedLocations = (newLocation :: occupiedLocations).filterNot(_ == location)
      if (tuna.tBreed == 0) {
        val newBornTuna = new Tuna(tBreed)
        val newBornlocation = location
        this.tunaList = (newBornTuna, newBornlocation) :: tunaList
        this.occupiedLocations = (newBornlocation :: occupiedLocations)
        tuna.tBreed = tBreed
      } else {
        tuna.tBreed = tuna.tBreed - 1
      }
    } else {
      //do nothing

    }



  }


  //move a shark to case in moore neighborhood
  def moveShark(shark: Shark, location: Location): Unit = {
    //check if the shark is dead
    if (shark.sEnergy == 0) {
      this.sharksList = sharksList.filterNot(_._1 == shark)
      this.occupiedLocations = occupiedLocations.filterNot(_ == location)
    } else{
      //choose a random new location from the moore neighborhood if it is empty if not do nothing starting with those occupied by tuna
      val mooreNeighborhood = location.mooreNeighborhood(this)
      val emptyMooreNeighborhood = mooreNeighborhood.filter(isLocationEmpty)
      val tunaMooreNeighborhood = mooreNeighborhood.filter(isLocationOccupiedByTuna)
      if (tunaMooreNeighborhood.nonEmpty) {
        val newLocation = tunaMooreNeighborhood(scala.util.Random.nextInt(tunaMooreNeighborhood.length))
        this.sharksList = (shark, newLocation) :: sharksList.filterNot(_._2 == location)
        this.occupiedLocations = (newLocation :: occupiedLocations).filterNot(_ == location)
        //eat tuna
        this.tunaList = tunaList.filterNot(_._2 == newLocation)
        shark.sEnergy = shark.sEnergy + 1
        if (shark.sBreed == 0) {
          val newBornShark = new Shark(sBreed,sEnergy )
          val newBornlocation = location
          this.sharksList = (newBornShark, newBornlocation) :: sharksList
          this.occupiedLocations = (newBornlocation :: occupiedLocations)
          shark.sBreed = sBreed
        } else {
          shark.sBreed = shark.sBreed - 1
        }

      } else if (emptyMooreNeighborhood.nonEmpty) {
        val newLocation = emptyMooreNeighborhood(scala.util.Random.nextInt(emptyMooreNeighborhood.length))
        this.sharksList = (shark, newLocation) :: sharksList.filterNot(_._2 == location)
        this.occupiedLocations = (newLocation :: occupiedLocations).filterNot(_ == location)
        shark.sEnergy = shark.sEnergy - 1
        if (shark.sBreed == 0) {
          val newBornShark = new Shark(sBreed, sEnergy)
          val newBornlocation = location
          this.sharksList = (newBornShark, newBornlocation) :: sharksList
          this.occupiedLocations = (newBornlocation :: occupiedLocations)
          shark.sBreed = sBreed
        } else {
          shark.sBreed = shark.sBreed - 1
        }
      } else {
        shark.sEnergy = shark.sEnergy - 1
        if (shark.sBreed > 0) {
          shark.sBreed = shark.sBreed - 1
        }
      }

    }

  }
}
