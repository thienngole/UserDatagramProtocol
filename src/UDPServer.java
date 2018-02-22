

import java.io.*;
import java.net.*;
import java.util.*;

public class UDPServer {
    public static void main(String[] args) throws IOException {

        final String INPUT_FILE = "inputDataServer.txt";
        File inputFile = new File(INPUT_FILE);

        final int numOfItems = 6;
        String[] itemId = new String[numOfItems];
        String[] itemDescription = new String[numOfItems];
        String[] unitPrice = new String[numOfItems];
        String[] inventory = new String[numOfItems];

        try {
            Scanner input = new Scanner(inputFile);

            while (input.hasNext()) {
                for (int i = 0; i < numOfItems; i++) {

                    String oneLine = input.nextLine();
                    String [] info = oneLine.split(",");

                    itemId[i] = info[0];
                    itemDescription[i] = info[1];
                    unitPrice[i] = info[2];
                    inventory[i] = info[3];
                }
            } // End while
        } // End try

        catch (FileNotFoundException ex) {
            System.out.println("\nERROR: The input file name " + inputFile
                    + " was not found.");
        } // End catch exception


        DatagramSocket udpServerSocket = null;

        //BufferedReader in = null;

        DatagramPacket udpPacket = null;
        DatagramPacket udpPacket2 = null;

        String fromClient = null;
        String toClient = null;

        boolean morePackets = true;

        byte[] buf = new byte[256];

        udpServerSocket = new DatagramSocket(5130);

        System.out.println("Server program is live.");

        while (morePackets) {
            try {

                // receive UDP packet from client
                udpPacket = new DatagramPacket(buf, buf.length);
                udpServerSocket.receive(udpPacket);

                fromClient = new String(
                        udpPacket.getData(), 0, udpPacket.getLength());

                int location;
                location = find(itemId, fromClient, numOfItems);

                toClient = itemId[location] + ", " +itemDescription[location]
                        + ", " + unitPrice[location] + ", " + inventory[location];

                // send the response to the client at "address" and "port"
                InetAddress address = udpPacket.getAddress();
                int port = udpPacket.getPort();
                byte[] buf2 = toClient.getBytes();
                udpPacket2 = new DatagramPacket(buf2, buf2.length, address, port);
                udpServerSocket.send(udpPacket2);

            } catch (IOException e) {
                e.printStackTrace();
                morePackets = false;
            }
        }

        udpServerSocket.close();

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
