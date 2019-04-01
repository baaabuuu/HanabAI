package dtu.AI.StateTree;

public class Score {
	/**
	 * File Author: Emil Bejder (s164161)
	 * 
	 * */
	public double weightedCertaintySum;
	public double weightedInformationValue;
	public double weightedBoardScore;
	public double weightedLife;
	public double weightedMaxScore;
	public double fireworkScore;
	public double weightedHandValue;
	public double beliefStateScore;
	private int depth;

	
	public Score (double weightedCertaintySum,double weightedInformationValue,double weightedBoardScore,double weightedLife, double fireworkScore,double weightedMaxScore, double weightedHandValue, int depth) {
		this.weightedCertaintySum = weightedCertaintySum;
		this.weightedInformationValue = weightedInformationValue;
		this.weightedBoardScore= weightedBoardScore;
		this.weightedLife = weightedLife;
		this.weightedMaxScore = weightedMaxScore;
		this.fireworkScore = fireworkScore;
		this.weightedHandValue = weightedHandValue;
		this.setDepth(depth);
	}
	
	
	public double getSum() {
		
		
		return this.weightedCertaintySum+this.weightedInformationValue+this.weightedBoardScore+this.weightedLife+this.weightedMaxScore+this.fireworkScore+this.weightedHandValue;
		
	}
	
	public Score(double beliefStateScore,int depth) {
		this.beliefStateScore = beliefStateScore;
		this.setDepth(depth);
	}
	
	public double getDepthCorrectedSum() {
		return (this.weightedCertaintySum+this.weightedInformationValue+this.weightedBoardScore+this.weightedLife+this.weightedMaxScore+this.fireworkScore+this.weightedHandValue)*getDepth();

	}


	public int getDepth() {
		return depth;
	}


	public void setDepth(int depth) {
		this.depth = depth;
	}
	
}
