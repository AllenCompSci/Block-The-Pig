import java.util.ArrayList;
import java.util.List;

public class pigPath {

	public pigPath() {
		// TODO Auto-generated constructor stub
	}
	public void dijikstra(square [][] game, int PigI, int PigJ){
		for(square[] nodes : game)
			for(square node : nodes)
				node.setPath(false);
		
		if(game[PigI][PigJ].isEdge())
			return;
		Integer count = 0;
		int added = 1;
		List <ArrayList<square>> Paths = new ArrayList<ArrayList<square>>();
		List <Integer> DISTANCE = new ArrayList <Integer>();
		boolean Blocked = false;
		boolean edgeReached = false;
		square start;
		int previousIndex = 0;
		int times = 0;
		Paths.add(new ArrayList<square>());
		Paths.get(0).add(game[PigI][PigJ]);
		DISTANCE.add(count);
		count ++;
		
		while(Blocked == false && edgeReached == false){
			times += added;
			added = 0;
			for(int i = previousIndex; i < times; i++){
				start = Paths.get(i).get(Paths.get(i).size()-1);
				for(square connection : start.getSurrounding()){
					if(connection != null){
						if(!connection.isImpassable() && !Paths.get(i).contains(connection)){
							Paths.add(new ArrayList<square>());
							Paths.get(Paths.size()-1).addAll(Paths.get(i));
							Paths.get(Paths.size()-1).add(connection);
							DISTANCE.add(count);
							if(!edgeReached)
								edgeReached = connection.isEdge();
							added ++;
						}
					}
			
				}
			
			
			}
			count ++;
			if(added == 0){
				Blocked = true;
			}
			previousIndex = times;
		}
		times += added;
		
		if(Blocked)
		{
			game[PigI][PigJ].setBlocked(true);
			return;
		}
		
		for(int i = previousIndex; i < times; i++){
			if(Paths.get(i).get(Paths.get(i).size()-1).isEdge()){
				//System.out.println(i + ". " + Paths.get(i) + " : " + DISTANCE.get(i));
				for(square node : Paths.get(i)){
					node.setPath(true);
				}
			}
		}
		
	}
}
