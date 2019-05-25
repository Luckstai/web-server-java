package view;
import server.HttpRequest;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Panel extends JFrame implements ActionListener {

    private static final int PORT_NUMBER = 12345;
    private JButton btnIniciar;
    private JButton btnParar;
    private ServerSocket socket;
    private Boolean statusServer = false;

    public Panel() {
        super ("WebServer Grupo 10");
        JLabel lblServidor = new JLabel("Servidor Web");
        JLabel lblStatus = new JLabel("Status: Ativo");
        JLabel lblUltimosEventos = new JLabel("Ultimos Eventos");
        btnIniciar = new JButton("Iniciar");
        btnParar = new JButton("Parar");
        JButton btnReiniciar = new JButton("Reiniciar");

        btnIniciar.addActionListener(this);
        btnParar.addActionListener(this);
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

        pnlBottom.add(btnIniciar);
        pnlBottom.add(btnParar);
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

        if(event.getSource() == btnIniciar){
            statusServer = true;
            try {
                socket = new ServerSocket(PORT_NUMBER);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("Server run in port " + PORT_NUMBER);
        }

        if(event.getSource() == btnParar) {
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