package dtu.AI.StateTree;

public class ScoreConfig {
	/**
	 * File Author: Emil Bejder (s164161)
	 * 
	 * */
	
	//touch this
	private static double confidenceWeight = 0.5; //Default 0.5
	private static double informationWeight =0.1; //Default 0.1
	private static double boardScoreWeight = 100; //Default 100
	private static double potentialscoreWeight = 0.1; //Default 0.1
	private static double lifeWeight  = 10.0; //Default 10
	private static double fireworkScoreWeight =10; //Default 10
	private static double handValueWeight = 2; //Default 2
	
	//Don't touch this
	private static int fireworkScoreFunction = 1; // 0 = don't consider, 1 sum of sqrt
	
	
	
	
//Getters and Setters	
	
	public static double getCertaintyWeight() {
		return confidenceWeight;
	}
	public static void setCertaintyWeight(double certaintyWeight) {
		ScoreConfig.confidenceWeight = certaintyWeight;
	}
	public static double getBoardScoreWeight() {
		return boardScoreWeight;
	}
	public static void setBoardScoreWeight(double boardScoreWeight) {
		ScoreConfig.boardScoreWeight = boardScoreWeight;
	}
	public static double getLifeWeight() {
		return lifeWeight;
	}
	public static void setLifeWeight(double lifeWeight) {
		ScoreConfig.lifeWeight = lifeWeight;
	}
	public static int getFireworkScoreFunction() {
		return fireworkScoreFunction;
	}
	public static void setFireworkScoreFunction(int fireworkScoreFunction) {
		ScoreConfig.fireworkScoreFunction = fireworkScoreFunction;
	}
	public static double getInformationWeight() {
		return informationWeight;
	}
	public static void setInformationWeight(double informationWeight) {
		ScoreConfig.informationWeight = informationWeight;
	}
	public static double getPotentialscoreWeight() {
		return potentialscoreWeight;
	}
	public static void setPotentialscoreWeight(double potentialscoreWeight) {
		ScoreConfig.potentialscoreWeight = potentialscoreWeight;
	}
	public static double getFireworkScoreWeight() {
		return fireworkScoreWeight;
	}
	public static void setFireworkScoreWeight(double fireworkScoreWeight) {
		ScoreConfig.fireworkScoreWeight = fireworkScoreWeight;
	}
	public static double getHandValueWeight() {
		return handValueWeight;
	}
	public static void setHandValueWeight(double handValueWeight) {
		ScoreConfig.handValueWeight = handValueWeight;
	}
	

}
