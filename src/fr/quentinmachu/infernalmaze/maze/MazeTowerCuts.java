package fr.quentinmachu.infernalmaze.maze;

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import fr.quentinmachu.infernalmaze.graph.Graph;
import fr.quentinmachu.infernalmaze.graph.Node;

public class MazeTowerCuts {
	private int width;
	private int height; 
	private int depth;
	private int minSizeMazeWidth;
	private int minSizeMazeHeight;
	
	private Point origin;
	private Point end;
	
	private int Version = 0;
	public MazeLevel[] Tower;
	public Graph G;
	
	private Maze[] mazeToPrint;
	
	public MazeTowerCuts(int width, int height, int minSizeMazeWidth, int minSizeMazeHeight){
		this(width, height, minSizeMazeWidth, minSizeMazeHeight, new Point(0,0) );
	}
	
	public MazeTowerCuts(int width, int height, int minSizeMazeWidth, int minSizeMazeHeight, Point origin){
		this(width, height, 2 , minSizeMazeWidth, minSizeMazeHeight, origin);
	}
	
	public MazeTowerCuts(int width, int height, int depth, int minSizeMazeWidth, int minSizeMazeHeight, Point origin) {
		if(width<=0 || height<=0 || depth<1 || origin.x<0 || origin.x>=width || origin.y<0 || origin.y>=height)
			throw new IllegalArgumentException();
		this.origin = origin;
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.minSizeMazeHeight = minSizeMazeHeight;
		this.minSizeMazeWidth = minSizeMazeWidth;
		this.G = new Graph();
		//Step 1 on détermine le premier lvl de coupes à effectuer
		//Step 2 on Créer depth tableau 2D pour stocker chaque maze.
		//Step 3 on merge chaque maze pour un même level
		//Step 4 on créer un graph avec un Node pour chaque petit maze
		//Step 5 on Créer les connexions entre chaque node
		//Step 6 on génère les entrée sorties en parcourant le graph
		//Step 7 on détermine un chemin.
		generate();
		mazeToPrint = new Maze[Tower.length];
		for(int i = 0; i < Tower.length; i++){
			mazeToPrint[i] = Tower[i].toMaze(width, height);
		}
		graphVertices();
		pathFinding();
		
		getVerticeByLevel();
		for(int i = 0; i < this.depth; i++){
			for(int j = 0; j < mazeToPrint[i].getToTopMaze().size(); j++)
			System.out.println("Tp vers le haut au niveau "+i+" : "+mazeToPrint[i].getToTopMaze().get(j).getX()+","+mazeToPrint[i].getToTopMaze().get(j).getY());
			for(int j = 0; j < mazeToPrint[i].getToBottomMaze().size();j++)
			System.out.println("Tp vers le bas au niveau "+i+" : "+mazeToPrint[i].getToBottomMaze().get(j).getX()+","+mazeToPrint[i].getToBottomMaze().get(j).getY());
		}
	}
	
	private void generate(){
		//Step 1
		//Number of X & Y cuts
		int nbrMaxXCuts = (width / minSizeMazeWidth) -1;
		int nbrMaxYCuts = (height / minSizeMazeHeight)-1;
		int nbrXCuts = ThreadLocalRandom.current().nextInt(0, nbrMaxXCuts+1);
		int nbrYCuts = ThreadLocalRandom.current().nextInt(0, nbrMaxYCuts+1);
		//int NbrXCuts = 1;
		//int NbrYCuts = 0;
		
		//TODO CHANGE THIS SHIT
		depth = nbrXCuts + nbrYCuts +1;
		
		final int[] xWidth = new int[nbrXCuts + 1];
	    final int[] yHeight = new int[nbrYCuts + 1];

	    int xGap, yGap;

	    if(nbrXCuts == 0) {
	    	xGap = 0; 
	    	xWidth[0] = this.width;
	    }else{
		    xGap = this.width - (nbrXCuts + 1) * this.minSizeMazeWidth;
		    for (int i = 0; i < nbrXCuts; i++) {
		      final int xIncrease = ThreadLocalRandom.current().nextInt(xGap + 1);
		      xWidth[i] = this.minSizeMazeWidth + xIncrease;
		      xGap -= xIncrease;
		      if(i != 0) xWidth[i] += xWidth[i-1];
		    }
		    xWidth[nbrXCuts] = this.minSizeMazeWidth + xGap+ xWidth[nbrXCuts-1];;
	    }

	    if(nbrYCuts == 0) {
	    	yGap = 0; 
	    	yHeight[0] = this.height;
	    }else{
		    yGap = this.height - (nbrYCuts + 1) * this.minSizeMazeHeight;
		    for (int i = 0; i < nbrYCuts; i++) {
		      final int yIncrease = ThreadLocalRandom.current().nextInt(yGap + 1);
		      yHeight[i] = this.minSizeMazeWidth + yIncrease;
		      yGap -= yIncrease;
		      if(i != 0) yHeight[i] += yHeight[i-1];
		    }
		    yHeight[nbrYCuts] = this.minSizeMazeHeight + yGap + yHeight[nbrYCuts-1];
	    }
		
		//for(int i = 0; i < yHeight.length; i++) System.out.println("yHeight["+i+"] = "+yHeight[i]);
		
		//On détermine le nbr de lvl de la tour
		int nbrTowerLevel = nbrXCuts + nbrYCuts +1;
		//On créer le tableau de la tour (avec les levels)
		Tower = new MazeLevel[nbrTowerLevel];
		Tower[0] = new MazeLevel(nbrYCuts +1,nbrXCuts+1);
		
/*
		for(int i =1; i < NbrTowerLvl; i++){
			Tower[i] = new MazeLevel(NbrXCuts,NbrYCuts);*/
			/*
			int XorY;
			XorY = ThreadLocalRandom.current().nextInt(0,1);
			if(XorY == 0 && ){
				NbrXCutsToFill--;
			}else{
				XorY = 1;
			}
			if(XorY == 1 && NbrXCutsToFill != 0){
				NbrYCutsToFill--;
			}
			
			if(NbrXCutsToFill == 0 && NbrYCutsToFill ==0){
				break;
			}
			*/
			/*
		}*/
		
		
		/*Every Object is allocated. We have now to fill them with maze.*/
		int xSize = 0,ySize = 0,XorY = 0,cutToLose = 0, posCutToLose = 0;
		int[][] xWidthCutted = null; 
		int[][] yHeightCutted = null;
		xWidthCutted = new int[Tower.length][];
		yHeightCutted = new int[Tower.length][];
		ArrayList<Integer> xCutsToPick = new ArrayList<Integer>();
		ArrayList<Integer> yCutsToPick = new ArrayList<Integer>();
		if(nbrXCuts == 0){
			xCutsToPick.clear();
		}else{
			for(int i = 0; i < nbrXCuts; i++){
				xCutsToPick.add(i);
			}
		}
		if(nbrYCuts == 0){
			yCutsToPick.clear();
		}else{
			for(int i = 0; i < nbrYCuts; i++){
				yCutsToPick.add(i);
			}
		}
		for(int i = 1; i < nbrTowerLevel; i++){
				//We need to pick a cut to not chose.
				XorY = ThreadLocalRandom.current().nextInt(0,2);
				if(XorY == 0 && xCutsToPick.isEmpty()) XorY = 1;
				if(XorY == 1 && yCutsToPick.isEmpty()) XorY = 0;
				if(XorY == 0){
					Tower[i] = new MazeLevel(nbrYCuts+1,nbrXCuts);
					if(xCutsToPick.size() == 1) posCutToLose = 0;
					else posCutToLose = ThreadLocalRandom.current().nextInt(0, xCutsToPick.size());
					cutToLose = xCutsToPick.get(posCutToLose);
					yHeightCutted[i] = yHeight;
					xWidthCutted[i] = new int[xWidth.length-1];
					for(int n = 0; n < xWidth.length; n ++){
						if(n < cutToLose){
							xWidthCutted[i][n] = xWidth[n];
						}else{
							if(n > cutToLose){
								xWidthCutted[i][n-1] = xWidth[n];
							}
						}
					}
				}else{
					Tower[i] = new MazeLevel(nbrYCuts,nbrXCuts+1);
					if(yCutsToPick.size() == 1) posCutToLose = 0; 
					else posCutToLose = ThreadLocalRandom.current().nextInt(0, yCutsToPick.size());
					cutToLose = yCutsToPick.get(posCutToLose);
					xWidthCutted[i] = xWidth;
					yHeightCutted[i] = new int[yHeight.length-1];
					for(int n = 0; n < yHeight.length; n ++){
						if(n < cutToLose){
							yHeightCutted[i][n] = yHeight[n];
						}else{
							if(n > cutToLose){
								yHeightCutted[i][n-1] = yHeight[n];
							}
						}
					}
			}
			if(XorY == 0) xCutsToPick.remove(posCutToLose);
			if(XorY == 1) yCutsToPick.remove(posCutToLose);
		}
			/*Pour les arcs il faut faire des if avec la structure de tableau 	[]->[]->[][]
			 * 																		[]->[][]
			 * 																		[]->[][]
			 * 																		[]->[][]
			 * 																	[]->[]->[][]
			 * 																	[]->[]->[][]
			 * Il faut remonter vers le niveau du dessus.
			 * Si c'est le premier c'est bon sinon il faut tester l'indice comme dans la fonction de merge de labi et tableau
			 * Une fois l'emplacement de la fin du maze déterminé et son début on peut déterminer si oui ou non il y a superposition
			 * Tester X et Y !!!!!!! EN MÊME TEMPS	
			 */
		for(int i = 0; i < nbrTowerLevel; i++){
			int height = 0;
			int width = 0;
			int[] xTemp;
			int[] yTemp;
			for(int j = 0; j < Tower[i].getMazes().length; j++){
				width = 0;
				for(int k = 0; k < Tower[i].getMazes()[j].length; k++){
					Maze theMaze = Tower[i].getMazes()[j][k];
					if(i == 0) {xTemp = xWidth; yTemp = yHeight;}else{xTemp = xWidthCutted[i]; yTemp = yHeightCutted[i];}
					if(k == 0){xSize = xTemp[k];}
						else{
							if(xTemp.length > 1) {xSize = xTemp[k] -  xTemp[k-1];} 
								else{ /*System.out.println("Merde");*/xSize = xTemp[0];}}
					if(j == 0){ySize = yTemp[j];}
						else{
							if(yTemp.length > 1) {ySize = yTemp[j] - yTemp[j-1];} 
								else{ /*System.out.println("Merde");*/ ySize = yTemp[0];}}
						/*System.out.println("xSize : "+ xSize + " | ySize :"+ZySize);*/
						Tower[i].getMazes()[j][k] = new Maze(xSize, ySize); //Création du premier lvl toutes les coupes
						Point p = new Point(width,height);
						Point q = new Point(width+xSize-1,height+ySize-1);
						Node E = G.createNode(i,p,q, Tower[i].getMazes()[j][k].getDeadEnds());
						G.addNode(E);
						width += xSize;
				}
				height += ySize;
			}
		}
		
		for(int i = 0; i < G.getNodeCount(); i++){
			System.out.println("Node "+ G.getNode(i).getId()+" est au niveau "+ G.getNode(i).getLevel());
			System.out.println("       topLeft : "+G.getNode(i).getTopLeft());
			System.out.println("       botRight: "+G.getNode(i).getBotRight());
		}
		
	}
	
	void graphVertices(){
		for(int i = 0; i < Tower.length-1; i++){
			for(int j = 0; j < G.getNodesByLevel(i).size(); j++){
				for(int k = 0; k < G.getNodesByLevel(i+1).size(); k++){
					//Case one both mazes are the same in term of size and emplacement
					Node upperMaze = G.getNodesByLevel(i).get(j);
					Node lowerMaze = G.getNodesByLevel(i+1).get(k);
					/*
					if(upperMaze.getTopLeft().getX()  == lowerMaze.getTopLeft().getX()  && 
					   upperMaze.getTopLeft().getY()  == lowerMaze.getTopLeft().getY()  &&
					   upperMaze.getBotRight().getX() == lowerMaze.getBotRight().getX() &&
					   upperMaze.getBotRight().getY() == lowerMaze.getBotRight().getY()){
						//Case one : fit perfectly
						Point tp = tpFinding(upperMaze, lowerMaze);
						if(tp.getX() != -1 && tp.getY() != -1) G.addVertice(upperMaze.getId(), lowerMaze.getId(), tp.getX(), tp.getY());
					}
					
					if(upperMaze.getTopLeft().getX()  == lowerMaze.getTopLeft().getX()  && 
					   upperMaze.getBotRight().getY() == lowerMaze.getBotRight().getY() &&
					   upperMaze.getTopLeft().getY()  == lowerMaze.getTopLeft().getY()  &&
					   upperMaze.getBotRight().getX() != lowerMaze.getBotRight().getX()
					   ){
							Point tp;
							if(upperMaze.getBotRight().getX() > lowerMaze.getBotRight().getX()){
								tp = tpFinding(lowerMaze, upperMaze);
							}else{
								tp = tpFinding(upperMaze, lowerMaze);
							}
							if(tp.getX() != -1 && tp.getY() != -1){
								G.addVertice(upperMaze.getId(), lowerMaze.getId(), tp.getX(), tp.getY());
							}else{
								
							}
						}
							
					if(upperMaze.getBotRight().getX()  == lowerMaze.getBotRight().getX()  && 
					   upperMaze.getBotRight().getY() == lowerMaze.getBotRight().getY()   &&
					   upperMaze.getTopLeft().getY()  == lowerMaze.getTopLeft().getY()    &&
					   upperMaze.getTopLeft().getX() != lowerMaze.getTopLeft().getX()){
						Point tp;
						if(upperMaze.getTopLeft().getX() < lowerMaze.getTopLeft().getX()){
							tp = tpFinding(lowerMaze, upperMaze);
						}else{
							tp = tpFinding(upperMaze, lowerMaze);
						}
						if(tp.getX() != -1 && tp.getY() != -1){
							G.addVertice(upperMaze.getId(), lowerMaze.getId(), tp.getX(), tp.getY());
						}else{
							
						}
					}
					
					if(upperMaze.getTopLeft().getX()  == lowerMaze.getTopLeft().getX()    && 
					   upperMaze.getTopLeft().getY() == lowerMaze.getTopLeft().getY()     &&
					   upperMaze.getBotRight().getX()  == lowerMaze.getBotRight().getX()  &&
					   upperMaze.getBotRight().getY() != lowerMaze.getBotRight().getY()){
						Point tp;
						if(upperMaze.getBotRight().getY() > lowerMaze.getBotRight().getY()){
							tp = tpFinding(lowerMaze, upperMaze);
						}else{
							tp = tpFinding(upperMaze, lowerMaze);
						}
						if(tp.getX() != -1 && tp.getY() != -1){
							G.addVertice(upperMaze.getId(), lowerMaze.getId(), tp.getX(), tp.getY());
						}else{
							
						}
					}
					
					if(upperMaze.getBotRight().getX()  == lowerMaze.getBotRight().getX()  && 
					   upperMaze.getBotRight().getY() == lowerMaze.getBotRight().getY()   &&
					   upperMaze.getTopLeft().getX()  == lowerMaze.getTopLeft().getX()    &&
					   upperMaze.getTopLeft().getY() != lowerMaze.getTopLeft().getY()){
						Point tp;
						if(upperMaze.getTopLeft().getY() < lowerMaze.getTopLeft().getY()){
							tp = tpFinding(lowerMaze, upperMaze);
						}else{
							tp = tpFinding(upperMaze, lowerMaze);
						}
						if(tp.getX() != -1 && tp.getY() != -1){
							G.addVertice(upperMaze.getId(), lowerMaze.getId(), tp.getX(), tp.getY());
						}else{
							
						}
					}
					
					
					if(upperMaze.getBotRight().getX()  == lowerMaze.getBotRight().getX()  && 
					   upperMaze.getTopLeft().getX() == lowerMaze.getTopLeft().getX() 	  &&
					   upperMaze.getBotRight().getY()  != lowerMaze.getBotRight().getY()  &&
					   upperMaze.getTopLeft().getY() != lowerMaze.getTopLeft().getY()){
						//At this moment we just know they are on the same line
						Point tp = null;
						if(upperMaze.getBotRight().getY() < lowerMaze.getBotRight().getY() && 
						   upperMaze.getTopLeft().getY()  > lowerMaze.getTopLeft().getY()){
							tp = tpFinding(upperMaze,lowerMaze);
						}else{
							if(lowerMaze.getBotRight().getY() < upperMaze.getBotRight().getY() && 
							   lowerMaze.getTopLeft().getY()  > upperMaze.getTopLeft().getY()){
									tp = tpFinding(lowerMaze,upperMaze);
							}else{
								if(upperMaze.getTopLeft().getY() < lowerMaze.getTopLeft().getY() && upperMaze.getBotRight().getY() > lowerMaze.getTopLeft().getY()){
									Point boundTopLeft  = lowerMaze.getTopLeft(); 
									Point boundBotRight = upperMaze.getBotRight();
									tp = tpFinding(lowerMaze,upperMaze,boundTopLeft,boundBotRight);
									if(tp.getX() == -1 && tp.getY() == -1){
										tp = tpRandom(lowerMaze, upperMaze, boundTopLeft, boundBotRight);
									}
								}else{
									if(upperMaze.getTopLeft().getY() > lowerMaze.getTopLeft().getY() && upperMaze.getTopLeft().getY() < lowerMaze.getBotRight().getY()){
										Point boundTopLeft  = upperMaze.getTopLeft(); 
										Point boundBotRight = lowerMaze.getBotRight();
										tp = tpFinding(lowerMaze,upperMaze,boundTopLeft,boundBotRight);
										if(tp.getX() == -1 && tp.getY() == -1){
											tp = tpRandom(lowerMaze, upperMaze, boundTopLeft, boundBotRight);
										}
									}
								}
							}
						}
						if(tp != null){
							if(tp.getX() != -1 && tp.getY() != -1){
								G.addVertice(upperMaze.getId(), lowerMaze.getId(), tp.getX(), tp.getY());
							}else{
								//They might not be any common parts
							}
						}else{
						
						}
					}
					
					
					
					if(upperMaze.getBotRight().getY()  == lowerMaze.getBotRight().getY()  && 
						upperMaze.getTopLeft().getY() == lowerMaze.getTopLeft().getY() 	  &&
						upperMaze.getBotRight().getX()  != lowerMaze.getBotRight().getX() &&
						upperMaze.getTopLeft().getX() != lowerMaze.getTopLeft().getX()){
								//At this moment we just know they are on the same line
								Point tp = null;
								if(upperMaze.getBotRight().getX() < lowerMaze.getBotRight().getX() && 
								   upperMaze.getTopLeft().getX()  > lowerMaze.getTopLeft().getX()){
									tp = tpFinding(upperMaze,lowerMaze);
								}else{
									if(lowerMaze.getBotRight().getX() < upperMaze.getBotRight().getX() && 
									   lowerMaze.getTopLeft().getX()  > upperMaze.getTopLeft().getX()){
											tp = tpFinding(lowerMaze,upperMaze);
									}else{
										if(upperMaze.getTopLeft().getX() < lowerMaze.getTopLeft().getX() && upperMaze.getBotRight().getX() > lowerMaze.getTopLeft().getX()){
											Point boundTopLeft  = lowerMaze.getTopLeft(); 
											Point boundBotRight = upperMaze.getBotRight();
											tp = tpFinding(lowerMaze,upperMaze,boundTopLeft,boundBotRight);
											if(tp.getX() == -1 && tp.getY() == -1){
												tp = tpRandom(lowerMaze, upperMaze, boundTopLeft, boundBotRight);
											}
										}else{
											if(upperMaze.getTopLeft().getX() > lowerMaze.getTopLeft().getX() && upperMaze.getTopLeft().getX() < lowerMaze.getBotRight().getX()){
												Point boundTopLeft  = upperMaze.getTopLeft(); 
												Point boundBotRight = lowerMaze.getBotRight();
												tp = tpFinding(lowerMaze,upperMaze,boundTopLeft,boundBotRight);
												if(tp.getX() == -1 && tp.getY() == -1){
													tp = tpRandom(lowerMaze, upperMaze, boundTopLeft, boundBotRight);
												}
											}
										}
									}
								}
								if(tp != null){
									if(tp.getX() != -1 && tp.getY() != -1){
										G.addVertice(upperMaze.getId(), lowerMaze.getId(), tp.getX(), tp.getY());
									}else{
										
									}
								}else{
									
								}
							}
					*/
					Point topLeft = new Point(-1,-1);
					Point botRight = new Point(-1,-1);
					if(upperMaze.getTopLeft().getX() <= lowerMaze.getTopLeft().getX() && lowerMaze.getTopLeft().getX() <= upperMaze.getBotRight().getX()){
						topLeft.x = lowerMaze.getTopLeft().x;
					}else{
						if(upperMaze.getTopLeft().getX() >= lowerMaze.getTopLeft().getX() && upperMaze.getTopLeft().getX() <= lowerMaze.getBotRight().getX()){
							topLeft.x = upperMaze.getTopLeft().x;
						}
					}
					
					if(upperMaze.getTopLeft().getY() <= lowerMaze.getTopLeft().getY() && lowerMaze.getTopLeft().getY() <= upperMaze.getBotRight().getY()){
						topLeft.y = lowerMaze.getTopLeft().y;
					}else{
						if(upperMaze.getTopLeft().getY() >= lowerMaze.getTopLeft().getY() && upperMaze.getTopLeft().getY() <= lowerMaze.getBotRight().getY()){
							topLeft.y = upperMaze.getTopLeft().y;
						}
					}
					
					if(upperMaze.getBotRight().getX() >= lowerMaze.getBotRight().getX() && lowerMaze.getBotRight().getX() >= upperMaze.getTopLeft().getX()){
						botRight.x = lowerMaze.getBotRight().x;
					}else{
						if(upperMaze.getBotRight().getX() <= lowerMaze.getBotRight().getX() && upperMaze.getBotRight().getX() >= lowerMaze.getTopLeft().getX()){
							botRight.x = upperMaze.getBotRight().x;
						}
					}
					
					if(upperMaze.getBotRight().getY() >= lowerMaze.getBotRight().getY() && lowerMaze.getBotRight().getY() >= upperMaze.getTopLeft().getY()){
						botRight.y = lowerMaze.getBotRight().y;
					}else{
						if(upperMaze.getBotRight().getY() <= lowerMaze.getBotRight().getY() && upperMaze.getBotRight().getY() >= lowerMaze.getTopLeft().getY()){
							botRight.y = upperMaze.getBotRight().y;
						}
					}
					if(topLeft.getX() != -1 && topLeft.getY() != -1 && botRight.getX() != -1 && botRight.getY() != -1){
						Point tp = tpFinding(lowerMaze,upperMaze,topLeft,botRight);
						G.addVertice(upperMaze.getId(), lowerMaze.getId(), tp.getX(), tp.getY());
					}
					
						//We check one by one the 4 corners to find one not already used to use it for the new connection.
						//Starting with the current maze. We will check the lowermaze case if the current has an available case
						
						
						/*
						boolean alreadyUsed = true;
						for(int l = 0; l < upperMaze.getLinks().size(); l++){
							 //First case the botRight one
							if(upperMaze.getLink(l).getGate().getX() == upperMaze.getBotRight().getX() && upperMaze.getLink(l).getGate().getY() == upperMaze.getBotRight().getY()){
								 //Is available for the upperMaze need to check about the lower one
								for(int m = 0; m < lowerMaze.getLinks().size(); m++){
									if(lowerMaze.getLink(m).getGate().getX() == upperMaze.getBotRight().getX() && lowerMaze.getLink(m).getGate().getY() == upperMaze.getBotRight().getY()){
										alreadyUsed = true;
									}else{
										alreadyUsed = false;
										G.addVertice(upperMaze.getId(), lowerMaze.getId(), upperMaze.getBotRight().getX(), upperMaze.getBotRight().getY());
										break;
									}
								}
								if(alreadyUsed == false) break;
							}
							//Second case the topLeft one
							if(i != 0 && j != 0 && k != 0){ 
								//Coz this is where we start. We cannot place one in (0,0) of top mazeLevel.
								if(upperMaze.getLink(l).getGate().getX() == upperMaze.getTopLeft().getX() && upperMaze.getLink(l).getGate().getY() == upperMaze.getTopLeft().getY()){
									 //Is available for the upperMaze need to check about the lower one
									for(int m = 0; m < lowerMaze.getLinks().size(); m++){
										if(lowerMaze.getLink(m).getGate().getX() == upperMaze.getTopLeft().getX() && lowerMaze.getLink(m).getGate().getY() == upperMaze.getTopLeft().getY()){
											alreadyUsed = true;
										}else{
											alreadyUsed = false;
											G.addVertice(upperMaze.getId(), lowerMaze.getId(), upperMaze.getTopLeft().getX(), upperMaze.getTopLeft().getY());
											break;
										}
									}
									if(alreadyUsed == false) break;
								}
							}
							
							//Third case the topRight one
							if(upperMaze.getLink(l).getGate().getX() == upperMaze.getTopLeft().getX() && upperMaze.getLink(l).getGate().getY() == upperMaze.getBotRight().getY()){
								 //Is available for the upperMaze need to check about the lower one
								for(int m = 0; m < lowerMaze.getLinks().size(); m++){
									if(lowerMaze.getLink(m).getGate().getX() == upperMaze.getTopLeft().getX() && lowerMaze.getLink(m).getGate().getY() == upperMaze.getBotRight().getY()){
										alreadyUsed = true;
										break;
									}else{
										alreadyUsed = false;
										G.addVertice(upperMaze.getId(), lowerMaze.getId(), upperMaze.getTopLeft().getX(), upperMaze.getBotRight().getY());
										break;
									}
								}
								if(alreadyUsed == false) break;
							}
							
							//Fourth case the botLeft one
							if(upperMaze.getLink(l).getGate().getX() == upperMaze.getBotRight().getX() && upperMaze.getLink(l).getGate().getY() == upperMaze.getTopLeft().getY()){
								 //Is available for the upperMaze need to check about the lower one
								for(int m = 0; m < lowerMaze.getLinks().size(); m++){
									if(lowerMaze.getLink(m).getGate().getX() == upperMaze.getBotRight().getX() && lowerMaze.getLink(m).getGate().getY() == upperMaze.getTopLeft().getY()){
										alreadyUsed = true;
									}else{
										alreadyUsed = false;
										G.addVertice(upperMaze.getId(), lowerMaze.getId(), upperMaze.getBotRight().getX(), upperMaze.getTopLeft().getY());
										break;
									}
								}
								if(alreadyUsed == false) break;
							}
							 //The last case, none works it's not normal. You're fucked
						}
					}else{
						if(upperMaze.getTopLeft().getX() == lowerMaze.getTopLeft().getX() 
								&& upperMaze.getTopLeft().getY() == lowerMaze.getTopLeft().getY()
								&& upperMaze.getTopLeft().getY() == lowerMaze.getBotRight().getY()
								&& upperMaze.getBotRight().getX() == lowerMaze.getBotRight().getX()){
							//We check the case when we cutted the right side of a maze
							//We are going to try to put it in the botLeft side of the maze or the topLeft one
							boolean alreadyUsed = true;
							for(int l = 0; l < upperMaze.getLinks().size(); l++){
								
							}
						}else{
							
						}
						
					}*/
				}
			}
		}
		
		System.out.println("-----------VERTICES---------");
		System.out.println(G.getVerticeCount());
		for(int i = 0; i < G.getVerticeCount(); i++){
			System.out.println(G.getVertice(i).getIdNodeFrom()+" - "+ G.getVertice(i).getIdNodeTo());
		}
		
		
		//Now we have to check that there is no common point going up and down in the same maze.
		//If we find out there is one we will rand his position within the minSizeMaze
	}

	Point tpFinding(Node smallerMaze, Node biggerMaze){
		boolean alreadyUsed = true;
		while(alreadyUsed == true){
			int rand = ThreadLocalRandom.current().nextInt(0,smallerMaze.getDeadEnds().size());
			Point tp = smallerMaze.getDeadEnd(rand);
			if(smallerMaze.getLinks().size() == 0 && biggerMaze.getLinks().size() == 0) return tp;
			if(smallerMaze.getLinks().size() == 0 && biggerMaze.getLinks().size() != 0){
				for(int m = 0; m < biggerMaze.getLinks().size(); m++){
					if(biggerMaze.getLink(m).getGate().getX() != tp.getX() || biggerMaze.getLink(m).getGate().getY() != tp.getY()){
						alreadyUsed = false;
						return tp;
					}
				}
			}
			if(smallerMaze.getLinks().size() != 0 && biggerMaze.getLinks().size() == 0){
				for(int m = 0; m < smallerMaze.getLinks().size(); m++){
					if(smallerMaze.getLink(m).getGate().getX() != tp.getX() || smallerMaze.getLink(m).getGate().getY() != tp.getY()){
						alreadyUsed = false;
						return tp;
					}
				}
			}
			if(smallerMaze.getLinks().size() != 0 && biggerMaze.getLinks().size() != 0)
			for(int l = 0; l < smallerMaze.getLinks().size(); l++){
				if(smallerMaze.getLink(l).getGate().getX() != tp.getX() || smallerMaze.getLink(l).getGate().getY() != tp.getY()){
					for(int m = 0; m < biggerMaze.getLinks().size(); m++){
						if(biggerMaze.getLink(m).getGate().getX() != tp.getX() || biggerMaze.getLink(m).getGate().getY() != tp.getY()){
							alreadyUsed = false;
							return tp;
						}
					}
				}
			}
		}
		Point tp = new Point(-1,-1);
		return tp;
	}
	
	Point tpFinding(Node maze1, Node maze2, Point topLeft, Point botRight){
		boolean alreadyUsed = true;
		Point tp = new Point(-1,-1);
		ArrayList<Point> commonPartDeadEnds = new ArrayList<Point>();
		for(int i = 0; i < maze1.getDeadEnds().size(); i++){
			if(maze1.getDeadEnd(i).getX() >= topLeft.getX() && maze1.getDeadEnd(i).getX() <= botRight.getX() &&
				  maze1.getDeadEnd(i).getY() >= topLeft.getY() && maze1.getDeadEnd(i).getY() <= botRight.getY()){
				commonPartDeadEnds.add(maze1.getDeadEnd(i));
			}
		}
		for(int i = 0; i < maze2.getDeadEnds().size(); i++){
			if(maze2.getDeadEnd(i).getX() >= topLeft.getX() && maze2.getDeadEnd(i).getX() <= botRight.getX() &&
				  maze2.getDeadEnd(i).getY() >= topLeft.getY() && maze2.getDeadEnd(i).getY() <= botRight.getY()){
				commonPartDeadEnds.add(maze2.getDeadEnd(i));
			}
		}
		if(commonPartDeadEnds.size() == 0){
			commonPartDeadEnds.add(topLeft);
			commonPartDeadEnds.add(botRight);
			Point temp = new Point(topLeft.x, botRight.y);
			commonPartDeadEnds.add(temp);
			temp = new Point(botRight.x, topLeft.y);
			commonPartDeadEnds.add(temp);
		}
		while(alreadyUsed == true){
			tp = commonPartDeadEnds.get(rnd.nextInt(0,commonPartDeadEnds.size()));
			if(maze1.getLinks().size() == 0 && maze2.getLinks().size() == 0) return tp;
			if(maze2.getLinks().size() == 0 && maze1.getLinks().size() != 0){
				for(int m = 0; m < maze1.getLinks().size(); m++){
					if(maze1.getLink(m).getGate().getX() != tp.getX() || maze1.getLink(m).getGate().getY() != tp.getY()){
						alreadyUsed = false;
						return tp;
					}
				}
			}
			if(maze2.getLinks().size() != 0 && maze1.getLinks().size() == 0){
				for(int m = 0; m < maze2.getLinks().size(); m++){
					if(maze2.getLink(m).getGate().getX() != tp.getX() || maze2.getLink(m).getGate().getY() != tp.getY()){
						alreadyUsed = false;
						return tp;
					}
				}
			}
			
			if(maze2.getLinks().size() != 0 && maze1.getLinks().size() != 0)
			for(int l = 0; l < maze1.getLinks().size(); l++){
				if(maze1.getLink(l).getGate().getX() != tp.getX() || maze1.getLink(l).getGate().getY() != tp.getY()){
					for(int m = 0; m < maze2.getLinks().size(); m++){
						if(maze2.getLink(m).getGate().getX() != tp.getX() || maze2.getLink(m).getGate().getY() != tp.getY()){
							alreadyUsed = false;
							return tp;
						}
					}
				}
			}
		}
		tp = new Point(-1,-1);
		return tp;
	}
	
	
	Point tpRandom(Node maze1, Node maze2, Point topLeft, Point botRight){
		boolean alreadyUsed = true;
		Point tp = new Point(-1,-1);
		while(alreadyUsed == true){
			tp.setLocation((double)ThreadLocalRandom.current().nextInt(topLeft.x,botRight.x+1),(double)ThreadLocalRandom.current().nextInt(topLeft.y,botRight.y+1));
			for(int l = 0; l < maze1.getLinks().size(); l++){
				if(maze1.getLink(l).getGate().getX() != tp.getX() || maze1.getLink(l).getGate().getY() != tp.getY()){
					for(int m = 0; m < maze2.getLinks().size(); m++){
						if(maze2.getLink(m).getGate().getX() != tp.getX() || maze2.getLink(m).getGate().getY() != tp.getY()){
							alreadyUsed = false;
							return tp;
						}
					}
				}
			}
		}
		tp = new Point(-1,-1);
		return tp;
	}
	
	void pathFinding(){
		int pred = 0;
		int inf = Integer.MAX_VALUE;
		int[] dist = new int[G.getNodeCount()];
		for(int i = 1; i < G.getNodeCount(); i++){
			dist[i] = inf;
		}
		dist[0] = 0;
		ArrayList<Node> Q = new ArrayList<Node>();
		ArrayList<Integer> Visited = new ArrayList<Integer>();
		Visited.add(0);
		for(int i = 0; i < G.getNode(0).getNextNodes().size(); i++){
			Q.add(G.getNode(G.getNode(0).getNextNodes().get(i)));
		}
		
		while(!Q.isEmpty()){
			int nodeIdToVisit = Q.get(0).getId();
			Node nodeToVisit = Q.get(0);
			Visited.add(nodeIdToVisit);
			for(int i = 0; i < nodeToVisit.getNextNodes().size(); i++){
				if(!Visited.contains(nodeToVisit.getNextNodes().get(i))){
					Q.add(G.getNode(nodeToVisit.getNextNodes().get(i)));
				}
			}
			if(dist[nodeIdToVisit] > dist[pred]+1) dist[nodeIdToVisit] = dist[pred]+1;
			pred = nodeIdToVisit;
			Q.remove(0);
		}
		System.out.print("Noeuds visités : {");
		for(int i = 0; i < Visited.size(); i++){
			System.out.print(" "+Visited.get(i)+" ");
		}
		System.out.println("}");
		System.out.println("Distances depuis le node 0");
		for(int i = 0; i < dist.length; i++){
			System.out.println("Node "+i+" :"+dist[i]);
		}
		System.out.println("");
	}
	
	void getVerticeByLevel(){
		for(int i = 0; i < Tower.length; i++){
			for(int j = 0; j < G.getNodeCountByLevel(i); j++){
				for(int k = 0; k < G.getNodesByLevel(i).get(j).getLinks().size(); k++){
					if(G.getNodesByLevel(i).get(j).getLink(k).getIdNodeFrom() == G.getNodesByLevel(i).get(j).getId()){
						//C'est le node du dessus.
						mazeToPrint[i].addToBottomMaze(G.getNodesByLevel(i).get(j).getLink(k).getGate());
					}else{
						//C'est le node du dessous
						mazeToPrint[i].addToTopMaze(G.getNodesByLevel(i).get(j).getLink(k).getGate());
					}
				}
			}
		}
	}
	
	public String toString() {
		String s = "";
		ArrayList<Point> NullArray = new ArrayList<Point>();
		s += "<MazeTower width="+width+" height="+height+" depth="+depth+" origin=("+origin.x+","+origin.y+")>\n";//end=("+end.x+","+end.y+")
		if(Version == 1){
			for(int d = depth-1; d>=0; d--)
				for(int i = 0; i < Tower[d].getMazes().length; i++)
					for(int j = 0; j < Tower[d].getMazes()[i].length; j++){
						Maze theMaze = Tower[d].getMazes()[i][j];
						if(d == 0){
							//s+= theMaze.toString(NullArray, toBottomMaze[d]);
						}else if(d == depth-1){
							//s+= theMaze.toString(toBottomMaze[d-1],NullArray);
						}else{
							//s += theMaze.toString(toBottomMaze[d-1], toBottomMaze[d]);
						}
					}
		}else{
			for(int d = depth-1; d>=0; d--)
				/*for(int i = 0; i < Tower[d].getMazes().length; i++)
					for(int j = 0; j < Tower[d].getMazes()[i].length; j++)*/
						s+= mazeToPrint[d];
		}
		s += "</MazeTower>";
		
		return s;
	}
}
