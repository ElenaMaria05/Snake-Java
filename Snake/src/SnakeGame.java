import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.TextLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {
    private final int latime;
    private final int inaltime;
    private final int dimensiuneCelule;
    private final Random random = new Random();

    private static final int FRAME_RATE = 20;
    private boolean jocInceput = false;
    private boolean jocTerminat = false;

    private Directie directie=Directie.DREAPTA;
    private Directie directieNoua = Directie.DREAPTA;

    private final List<coordonata> snake = new ArrayList<>();
    private coordonata mancare;


    public SnakeGame(final int latime , final int inaltime) {
        super();
        this.latime = latime;
        this.inaltime = inaltime;
        this.dimensiuneCelule=latime/(FRAME_RATE*2);

        setPreferredSize(new Dimension(latime, inaltime));

        Color background = new Color(100, 150, 10);
        setBackground(background);
    }
    public void startGame(){

        reseteazaJoc();
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed( final KeyEvent e) {
                handleKeyEvent(e.getKeyCode());
            }
        });
        new Timer(1000/FRAME_RATE, this).start();

    }

    private void handleKeyEvent(final int keyCode) {
        if(!jocInceput){
            if(keyCode== KeyEvent.VK_SPACE){
                jocInceput=true;
            }
        }
        else if(!jocTerminat){
            switch (keyCode){
                case KeyEvent.VK_W:
                    if(directie!=Directie.JOS){
                        directieNoua = Directie.SUS;
                    }
                    break;
                case KeyEvent.VK_S:
                    if(directie!=Directie.SUS){
                        directieNoua = Directie.JOS;
                    }
                    break;
                case KeyEvent.VK_A:
                    if(directie!=Directie.DREAPTA){
                        directieNoua = Directie.STANGA;
                    }
                    break;
                case KeyEvent.VK_D:
                    if(directie!=Directie.STANGA){
                        directieNoua = Directie.DREAPTA;
                    }
                    break;
            }
        }
        else if(keyCode==KeyEvent.VK_SPACE){
            jocInceput=false;
            jocTerminat=false;
            reseteazaJoc();
        }
    }

    private void reseteazaJoc(){
        snake.clear();
        snake.add(new coordonata(latime/2,inaltime/2));
        generareMancare();
    }

    private void generareMancare(){
        do{
            mancare= new coordonata(random.nextInt(latime/dimensiuneCelule)*dimensiuneCelule,random.nextInt(inaltime/dimensiuneCelule)*dimensiuneCelule);
        }while(snake.contains(mancare));
    }

    @Override
    protected void paintComponent(Graphics obiecte_grafice) {
        super.paintComponent(obiecte_grafice);

        if(!jocInceput){
            printeazaMesaj(obiecte_grafice,"Apasa SPACE pentru a incepe");
        }
        else {
            Color snakeColor = new Color(20, 100, 60);
            obiecte_grafice.setColor(snakeColor);
            for(final var punct:snake){
                obiecte_grafice.fillOval(punct.x,punct.y,dimensiuneCelule+5,dimensiuneCelule+5);
            }
            Color foodColor = new Color(150,30,30);
            obiecte_grafice.setColor(foodColor);
            obiecte_grafice.fillOval(mancare.x, mancare.y,dimensiuneCelule,dimensiuneCelule);
        }
        if(jocTerminat){
            printeazaMesaj(obiecte_grafice,"Ai obtinut scorul: "+snake.size());

        }

    }

    private void printeazaMesaj( final  Graphics obiecte_grafice, final String mesaj) {
        obiecte_grafice.setColor(Color.white);
        obiecte_grafice.setFont(obiecte_grafice.getFont().deriveFont(30F));
        final var obiecte2D=(Graphics2D) obiecte_grafice;
        final var frc = obiecte2D.getFontRenderContext();
       for(final var linie : mesaj.split("\n")){
            final var layout = new TextLayout(linie, obiecte_grafice.getFont(),frc);
            layout.draw(obiecte2D,100,100);
        }
    }

    private void move(){
        final coordonata cap = snake.getFirst();
        final  coordonata capNou = switch (directie){
            case SUS -> new coordonata(cap.x,cap.y - dimensiuneCelule);
            case JOS -> new coordonata(cap.x,cap.y + dimensiuneCelule);
            case STANGA -> new coordonata(cap.x - dimensiuneCelule,cap.y );
            case DREAPTA -> new coordonata(cap.x + dimensiuneCelule,cap.y );
        };
        snake.addFirst(capNou);


        if(capNou.equals(mancare)){
            generareMancare();
        }
        else if(aTrecutDeMargini(capNou)){
            coliziuneMargini();
            snake.removeLast();
        }
        else if(coliziuneCorp()){
            jocTerminat=true;
        }
        else {
            snake.removeLast();
        }
        directie=directieNoua;
    }

    private boolean aTrecutDeMargini(coordonata capNou) {
        return capNou.x < 0 || capNou.x >= latime || capNou.y < 0 || capNou.y >= inaltime;
    }

    private void coliziuneMargini() {
        final coordonata cap = snake.getFirst();
        int newX = cap.x;
        int newY = cap.y;

        if (cap.x < 0) {
            newX = latime - dimensiuneCelule;
        } else if (cap.x >= latime) {
            newX = 0;
        }
        if (cap.y < 0) {
            newY = inaltime - dimensiuneCelule;
        } else if (cap.y >= inaltime) {
            newY = 0;
        }
        snake.set(0, new coordonata(newX, newY));
    }


    private boolean coliziuneCorp() {
        final coordonata corpFaraCap = snake.removeFirst();
        boolean coliziuneCorp = false;
        for (coordonata segment : snake) {
            if (corpFaraCap.equals(segment)) {
                coliziuneCorp = true;
                break;
            }
        }
        snake.addFirst(corpFaraCap);
        return coliziuneCorp;
    }
    public void actionPerformed(final ActionEvent e){
        if(jocInceput && !jocTerminat){
            move();
        }

        repaint();
    }

    private record coordonata(int x , int y ){ }

    private enum Directie {
        SUS,JOS,STANGA,DREAPTA
    }
}
