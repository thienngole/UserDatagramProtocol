
import java.io.*;
import java.net.*;
import java.util.*;

public class UDPClient {
    public static void main(String[] args) throws IOException {

        final String INPUT_FILE = "inputDataClient.txt";
        File inputDataFile = new File(INPUT_FILE);

        final int numOfItems = 6;
        String[] id = new String[numOfItems];
        String[] description = new String[numOfItems];

        long startTime = 0;
        long endTime = 0;
        long rttOfQuery = 0;

        try {
            Scanner inputFile = new Scanner(inputDataFile);

            while (inputFile.hasNext()) {
                for (int i = 0; i < numOfItems; i++) {

                    String line = inputFile.nextLine();
                    String info[] = line.split(",");

                    id[i] = info[0];
                    description[i] = info[1];
                }

            } // End while

        } // End try

        catch (FileNotFoundException ex) {
            System.out.println("\nERROR: The input file name " + inputDataFile
                    + " was not found.");

        } // End catch exception

        // creat a UDP socket
        DatagramSocket udpSocket = new DatagramSocket();

        BufferedReader keyBoard = new BufferedReader(new InputStreamReader(System.in));
        String fromServer;
        String fromUser;
        String userInput;
        String desAddress;

        System.out.println("Please enter the DNS or IP of the machine on which the server program runs.");
        desAddress = keyBoard.readLine();

        //Display the list of items
        System.out.println("List of item to choose.");
        System.out.printf("%-9s%-30s%n","Item ID","Item Description");

        for (int j = 0; j < numOfItems; j++) {
            System.out.printf("%-8s%-30s%n", id[j], description[j]);

        }


        //Ask user to input an item ID
        System.out.println("Please enter an item ID to run or 'Exit' to exit the program.");
        //userInput = keyBoard.readLine();


        while ((userInput = keyBoard.readLine()) != null) {

            if (userInput.equals("Exit")){
                break;
            }

            while( find(id, userInput ,numOfItems) == -1 ){

                System.out.println("ID " + userInput + " is INVALID. Please try again.");
                userInput = keyBoard.readLine();

            }

            System.out.println("ID " + userInput + " is VALID ");
            fromUser = userInput;

            //display user input
            System.out.println("From Client: " + fromUser);

            // send request
            InetAddress address = InetAddress.getByName(desAddress);
            byte[] buf = fromUser.getBytes();
            DatagramPacket udpPacket = new DatagramPacket(buf, buf.length, address, 5130);
            udpSocket.send(udpPacket);

            startTime = System.currentTimeMillis();

            // get response
            byte[] buf2 = new byte[256];
            DatagramPacket udpPacket2 = new DatagramPacket(buf2, buf2.length);
            udpSocket.receive(udpPacket2);

            endTime = System.currentTimeMillis();

            // display response
            fromServer = new String(udpPacket2.getData(), 0, udpPacket2.getLength());

            String itemId;
            String itemDescription;
            String unitPrice;
            String inventory;
            String outputLine = fromServer;
            String outputData[] = outputLine.split(",");

            rttOfQuery = endTime - startTime;
            itemId = outputData[0];
            itemDescription = outputData[1];
            unitPrice = outputData[2];
            inventory = outputData[3];

            System.out.printf("%-9s%-30s%-15s%-20s%-10s%n","Item ID","Item Description"
                    , "Unit Price", "Inventory", "RTT of Query");
            System.out.printf("%-8s%-30s%-15s%-20s%-10s%n", itemId, itemDescription, unitPrice, inventory, rttOfQuery + " ms");

            System.out.println("Please enter an item ID to run or 'Exit' to exit the program.");


        }

        udpSocket.close();
    }

    /*******************************************************************************************************************
     *
     * @param list
     * @param anEntry
     * @param numOfElements
     * @return
     */
    public static int find (String[] list, String anEntry, int numOfElements)
    {
        int location = -1;
        boolean result = false;
        int i = 0;

        while (!result && i < numOfElements)
        {
            if (anEntry.equals(list[i]))
            {
                location = i;
                result = true;
            }
            i++;
        }
        return location;
    }
}