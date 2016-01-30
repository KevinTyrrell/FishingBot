package Exceptions;

public class ApplicationNotFound extends Exception
{
	public ApplicationNotFound()
	{
		super("Specified application is not currently running.");
	}
}
