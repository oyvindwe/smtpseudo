package smtpseudo.smtp.message.transport;

import smtpseudo.smtp.message.exception.RetryException;

/**
 * Created by Haruhiko Nishi
 * Date: 2009/06/21
 * Time: 23:38:02
 */
public abstract class RetryStrategy {
	public static final int DEFAULT_NUMBER_OF_RETRIES=3;
	private int numberOfTriesLeft;

	public RetryStrategy() {
		this(DEFAULT_NUMBER_OF_RETRIES);
	}

	public RetryStrategy(int numberOfRetries){
		numberOfTriesLeft=numberOfRetries;
	}

	public boolean shouldRetry() {
		return 0<numberOfTriesLeft;
	}

    public int retriesLeft(){
        return numberOfTriesLeft;
    }

	public void tryRetry() throws RetryException {
		numberOfTriesLeft--;
		if (!shouldRetry()) {
			throw new RetryException("no more retries left");
		}
		waitUntilNextTry();
	}

	private void waitUntilNextTry() {
		long timeToWait=getTimeToWait();
		try {
			Thread.sleep(timeToWait);
		}
		catch (InterruptedException ignored){

        }
	}

    protected abstract long getTimeToWait();
}
