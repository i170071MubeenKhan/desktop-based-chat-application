package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class Server {

	private ServerSocket serverSocket;
	private DomainController domainController;
	private Scanner input;
	private boolean serverRunning;
	private Logger logger;

	public Server() {
		try {
			serverSocket = new ServerSocket(8080);
		} catch (IOException e) {
			e.printStackTrace();
		}
		domainController = new DomainController();
		input = new Scanner(System.in);
		serverRunning = true;
		this.logger = new Logger();
		this.CLIMenu();
		this.KickOff();
	}

	public void KickOff() {
		logger.ServerStarted();
		OnlineClientHandler newClient = null;
		while (serverRunning) {
			try {
				newClient = new OnlineClientHandler(serverSocket.accept(), domainController);
				synchronized (domainController) {
					domainController.onlineClients.add(newClient);
				}
				newClient.start();
			} catch (IOException e) {
			}
		}
	}

	public void CLIMenu() {
		Thread menu = new Thread() {
			@Override
			public void run() {
				System.out.println("\t\t\t\t\t|||===============================================|||");
				System.out.println("\t\t\t\t\t|||                  Server Side                  |||");
				System.out.println("\t\t\t\t\t|||===============================================|||");
				String choice = "";
				while (!choice.equals("0")) {
					printMainMenu();
					choice = input.nextLine();
					System.out.println();
					if (choice.equals("1")) {
						synchronized (domainController) {
							domainController.DisplayAllUsers();
						}
					} else if (choice.equals("2")) {
						synchronized (domainController) {
							domainController.DisplayOnlineClients();
						}
					}
					System.out.print("\nEnter any key to continue...");
					input.nextLine();
				}
				serverRunning = false;
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		menu.start();
	}

	private void printMainMenu() {
		System.out.println();
		System.out.println();
		System.out.println("\tMAIN MENU\n");
		System.out.println("1.   View All Registered Users");
		System.out.println("2.   View Online Clients");
		System.out.println("0.   Exit.");
		System.out.print("Your Choice: ");
	}

}
