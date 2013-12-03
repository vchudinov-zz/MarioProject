package vikrasim.agents.scanners;

public class Scanner extends MasterScanner {
	
	
	public Scanner(int length, int height, Dir scanDirection, ScannerType type) {
		super(length, height, scanDirection, type);
		// TODO Auto-generated constructor stub
	}

	protected void populatelLine(int[][] cellsToScan, int marioX, int marioY){
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
	
	
}
