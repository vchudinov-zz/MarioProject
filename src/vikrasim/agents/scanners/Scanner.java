package vikrasim.agents.scanners;

public class Scanner {
	public enum Dir {N, E, W, S, NE, NW, SE, SW};
	public enum ScannerType {ENEMY, ENVIRONMENT};
	
	int length;
	int height;
	Dir scanDirection;
	ScannerType type;
	private int[][] cellsToScan;
	
	public Scanner(int length, int height, Dir scanDirection, ScannerType type){
		if (!checkSize(length)) return;
		if (!checkSize(height)) return;
		
		this.length=length;
		this.height=height;
		this.scanDirection = scanDirection;
		this.type = type;
		
		findCellsToScan();
	}
	
	private boolean checkSize(int i){
		if (i < 1){
			System.out.println("Error width or height is to small");
			return false;
		}
		return true;
	}
	
	private void findCellsToScan(){
		this.cellsToScan = new int[length * height][2];
		int marioX = 9;
		int marioY = 9;
		
		if(height ==1){
			populatelLine(cellsToScan, marioX, marioY);
		} else {
			populateArea(cellsToScan, marioX, marioY);
		}		
	}
	/**
	 * Finds cells to scan in a line of length length from mario
	 * Example
	 * 	|M| | | | - line with length 3
	 * @param cellsToScan
	 * @param marioX
	 * @param marioY
	 */
	private void populatelLine(int[][] cellsToScan, int marioX, int marioY){
		int startX = 0;
		int startY = 0;
		int incrementX = 0;
		int incrementY = 0;
		
		switch(scanDirection){
		case N: startX = marioX; startY = marioY - 1; incrementX = 0; incrementY = -1; break;
		case S: startX = marioX; startY = marioY + 1; incrementX = 0; incrementY = 1; break;
		case E: startX = marioX + 1; startY = marioY; incrementX = 1; incrementY = 0; break;
		case W: startX = marioX - 1; startY = marioY; incrementX = -1; incrementY = 0; break;
		
		case NW: startX = marioX - 1; startY = marioY - 1; incrementX = -1; incrementY = -1; break;
		case NE: startX = marioX + 1; startY = marioY - 1; incrementX =  1; incrementY = -1; break;
		case SW: startX = marioX - 1; startY = marioY + 1; incrementX = -1; incrementY =  1; break;
		case SE: startX = marioX + 1; startY = marioY + 1; incrementX =  1; incrementY =  1; break;
		}
		
		
		int x = startX;
		int y = startY;
		for (int i = 0; i < length; i++){
			cellsToScan[i][0] = x;
			cellsToScan[i][1] = y;			
			x += incrementX;
			y += incrementY;
		}
	}
	/**
	 * Finds all the cells in the lenght x height area with mario in the corner
	 * |M| | |
	 * | | | | Example with height = 3 and length = 3, scanning SE
	 * | | | |
	 * @param cellsToScan
	 * @param marioX
	 * @param marioY
	 */
	private void populateArea (int[][] cellsToScan, int marioX, int marioY){
		int incrementX = 0;
		int incrementY = 0;
		
		switch(scanDirection){
		case N: incrementX = 0; incrementY = -1; break;
		case S: incrementX = 0; incrementY = 1; break;
		case E: incrementX = 1; incrementY = 0; break;
		case W: incrementX = -1; incrementY = 0; break;
		
		case NW: incrementX = -1; incrementY = -1; break;
		case NE: incrementX =  1; incrementY = -1; break;
		case SW: incrementX = -1; incrementY =  1; break;
		case SE: incrementX =  1; incrementY =  1; break;
		}
		
		int counter = 0;
		int x = marioX;
		for (int i = 0; i < length; i++){
			int y = marioY;
			for (int j = 0; j < height ; j++){
				cellsToScan[counter][0] = x;
				cellsToScan[counter][1] = y;
				y += incrementY;
				counter++;
			}
			x += incrementX;
		}
	}
	
	public double scan(byte[][] enemies, byte[][] levelScene){
		double result = (double) 0;
		switch (type){
		case ENEMY: result = scanNumberOfObjects(enemies); break;
		case ENVIRONMENT: result = scanDistanceToObject(levelScene); break;
		}
		
		return result;				
	}
	
	private double scanNumberOfObjects(byte[][] obervations){
		int numberOfObjects = 0;
		
		for (int i = 0; i < cellsToScan.length; i++){
			int x = cellsToScan[i][0];
			int y = cellsToScan[i][1];
			if (obervations[y][x] != 0){ //The data is saved as y,x
				numberOfObjects++;
			}
		}
		if (numberOfObjects == 0) {
			return (double) 0;
		} else {
			return (double) numberOfObjects / cellsToScan.length;
		}
	}
	
	private double scanDistanceToObject(byte[][] obervations){
		int i = 0;
		int x = 0;
		int y = 0;
		boolean objectFound = false;
		for (i = 0; i < cellsToScan.length; i++){
			x = cellsToScan[i][0];
			y = cellsToScan[i][1];
			if (obervations[y][x] != 0){//The data is saved as y,x
				objectFound = true;
				break;
			}
		}
		
		if (!objectFound) {
			return (double) 0;
		} else {
			
			return (double) normalizeDistance(x,y);
		}
	}
	
	private double normalizeDistance(int foundX, int foundY){
		int marioX = 9;
		int marioY = 9;
		double distance = cartesianDistance(foundX, foundY, marioX, marioY);
		
		int maxX = cellsToScan[cellsToScan.length - 1][0];
		int maxY = cellsToScan[cellsToScan.length - 1][1];
		double maxDistance = cartesianDistance(maxX, maxY, marioX, marioY);
		
		return (double) (maxDistance + 1 - distance) / maxDistance;
	}
	private double cartesianDistance(int x1, int y1, int x2, int y2){
		double result = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2 - y1, 2));
		return result;
	}
}
