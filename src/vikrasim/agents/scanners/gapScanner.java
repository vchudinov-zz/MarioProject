package vikrasim.agents.scanners;

public class gapScanner extends MasterScanner {
	
	
	public gapScanner(int length, int height, Dir scanDirection,
			ScannerType type) {
		super(length, height, scanDirection, type);
		// TODO Auto-generated constructor stub
	}

	protected void populatelLine(int[][] cellsToScan, int marioX, int marioY){
		int startX = 0;
		int startY = 0;
		int incrementX = 0;
		int incrementY = 0;
		
		//Scanning south
		startX = marioX + 1; 
		startY = marioY + 1; 
		incrementX = 0; 
		incrementY = 1; 		
		
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
