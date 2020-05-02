package Messages;

public class MsgAlgorithmMaxSum extends MsgAlgorithm{
    private int sender2;
    private int reciever2;
	
    public MsgAlgorithmMaxSum(int sender, int reciever, Object context, int timeStamp, double delay,
			double timeCreated, int sender2, int reciever2) {
		super(sender, reciever, context, timeStamp, delay, timeCreated);
		this.sender2 = sender2;
		this.reciever2 = reciever2;
	}

}
