package view;
import server.HttpRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@SuppressWarnings("serial")
public class Panel extends JFrame implements ActionListener {

    private static final int PORT_NUMBER = 12345;
    private JButton btnStart;
    private JButton btnStop;
    private ServerSocket socket;
    private Boolean statusServer = false;

    public Panel() {
        super ("WebServer Grupo 10");
        JLabel lblServidor = new JLabel("Servidor Web");
        JLabel lblStatus = new JLabel("Status: Ativo");
        JLabel lblUltimosEventos = new JLabel("Ultimos Eventos");
        btnStart = new JButton("Iniciar");
        btnStop = new JButton("Parar");
        JButton btnReiniciar = new JButton("Reiniciar");

        btnStart.addActionListener(this);
        btnStop.addActionListener(this);
        btnReiniciar.addActionListener(this);
        Container caixa = getContentPane();

        // TODO JList de teste, que posteriormente mostrará a lista de eventos do
        // WebServer - #AdilSou 04/03/19
        String eventos[] = { "Evento 1", "Evento 2", "Evento 3" };
        JList listUltimosEventos = new JList<String>(eventos);

        JPanel pnlTop = new JPanel(new GridLayout(3, 1));
        JPanel pnlMid = new JPanel(new GridLayout(1, 1));
        JPanel pnlBottom = new JPanel(new GridLayout(1, 3));

        JPanel pnlLinha1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlLinha1.add(lblServidor);
        JPanel pnlLinha2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlLinha2.add(lblStatus);
        JPanel pnlLinha3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlLinha3.add(lblUltimosEventos);

        pnlTop.add(pnlLinha1);
        pnlTop.add(pnlLinha2);
        pnlTop.add(pnlLinha3);

        pnlMid.add(listUltimosEventos);

        pnlBottom.add(btnStart);
        pnlBottom.add(btnStop);
        pnlBottom.add(btnReiniciar);

        caixa.add(pnlTop, BorderLayout.NORTH);
        caixa.add(pnlMid, BorderLayout.CENTER);
        caixa.add(pnlBottom, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setSize(300, 300);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        System.out.println("ENTROU NO METODO");
        if(event.getSource() == btnStart){
            statusServer = true;
            try {
                socket = new ServerSocket(PORT_NUMBER);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("Server run in port " + PORT_NUMBER);
        }

        if(event.getSource() == btnStop) {
            System.out.println("PARAR SERVIDOR");
            statusServer = false;
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        while (statusServer) {
            Socket connected = null;
            try {
                connected = socket.accept();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            (new HttpRequest(connected)).start();
        }
    }
}