/* ***************************************************************
* Autor............: Ademir de Jesus Reis Junior
* Matricula........: 202210327
* Inicio...........: 19/09/2023
* Ultima alteracao.: 19/09/2023
* Nome.............: Principal.java
* Funcao...........: Aplicacao JavaFX que simula trens em uma linha ferroviaria com o uso de Threads
*************************************************************** */

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Principal extends Application {
  private boolean g_isMoving = false; //Variavel global que define se os trens estao em movimento ou nao 
  private int g_playClicked = 0; //Varivavel global que checa os clicks no botao de play para dar start na thread ou play/pause na animacao da mesma

  public static boolean t1Paused = false; //Variavel global que verifica se o trem 1 esta pausado
  public static boolean t2Paused = false; //Variavel global que verifica se o trem 2 esta pausado
  public static int lock1 = 0; //Variavel de travamento para acesso aA primeira regiao critica
  public static int lock2 = 0; //Variavel de travamento para acesso aA segunda regiao critica

  @Override
  public void start(Stage primaryStage) throws Exception {
    /* PAINEL RAIZ */
      Pane root = new Pane();
      root.styleProperty().set("-fx-background-image: url('tela-fundo.jpg');");

      Scene scene = new Scene(root, 1000, 600);

      primaryStage.setTitle("A Vida é Trem Bala Parcero"); //Titulo da janela
      primaryStage.setScene(scene);
      primaryStage.setResizable(false); //Tamanho da janela nao alteravel
      primaryStage.centerOnScreen(); //Janela centralizada
    /* FIM PAINEL RAIZ */
    
    /* ESTRUTURA DA INTERFACE */
      VBox mainVBox = new VBox();
        Pane viewPane = new Pane(); //Painel de vizualizacao dos trens e trilhos
        HBox buttonsHBox = new HBox(); //Painel de botoes
          HBox firstBTNs = new HBox(); //Botoes play/pause e reset
            Button playBTN = new Button("Play");
            Button resetBTN = new Button("Reset");
          //end firstBTNs - play/pause e reset
          VBox secondBTNs = new VBox(); //Botoes de velocidade
            HBox velHBox1 = new HBox(); //Botoes de velocidade do Trem 1
              Button velDownBTN1 = new Button("-");
              Button velUpBTN1 = new Button("+");
              VBox velVBox1 = new VBox();
                Label velTitle1 = new Label("Velocidade do Trem 1");
                Label velValue1 = new Label("0 Km/h");
            //end velHBox1 - velocidade do trem 1
            HBox velHBox2 = new HBox(); //Botoes de velocidade do Trem 2
              Button velDownBTN2 = new Button("-");
              Button velUpBTN2 = new Button("+");
              VBox velVBox2 = new VBox();
                Label velTitle2 = new Label("Velocidade do Trem 2");
                Label velValue2 = new Label("0 Km/h");
            //end velHBox2 - velocidade do trem 2
          //end secondBTNs - botoes de velocidade
          Button changePositionBTN = new Button("Trocar Posições"); //Botao que troca a posicao dos trens
        //end buttonsHBox - painel de botoes
      //end mainVBox
    /* FIM DA ESTRUTURA DA INTERFACE */
    
    /* ELEMENTOS DA INTERFACE */
      /* TELA PRINCIPAL */
        //dimensoes:
        viewPane.setPrefWidth(700);
        viewPane.setPrefHeight(400);
        //posicionamento:
        viewPane.translateXProperty().set(150);
        viewPane.translateYProperty().set(25);
        //imagem de fundo:
        viewPane.styleProperty().set("-fx-background-image: url('background2.png');");
      /* FIM TELA PRINCIPAL */

      /* PAINEIS DOS TRENS */
        Pane train1 = new Pane();
        train1.styleProperty().set("-fx-background-image: url('train-img-20x20.png')");
        train1.setPrefWidth(20);
        train1.setPrefHeight(20);
        train1.translateXProperty().set(0);
        train1.translateYProperty().set(162);

        Pane train2 = new Pane();
        train2.styleProperty().set("-fx-background-image: url('train-img-20x20.png')");
        train2.setPrefWidth(20);
        train2.setPrefHeight(20);
        train2.translateXProperty().set(0);
        train2.translateYProperty().set(235);
      /* FIM PAINEIS DOS TRENS */

      /* PAINEL DOS TRILHOS, E CAMINHOS*/
        Pane rails = new Pane(); //Painel que contem os trilhos (imagem.png)
        rails.styleProperty().set("-fx-background-image: url('main-rails.png');\n-fx-background-position: center center;\n-fx-background-repeat: no-repeat;\n-fx-background-size: 100%;");
        //O painel ocupara toda a tela de visualizacao estando centralizada na vertical da mesma tela
        rails.setPrefWidth(700);
        rails.setPrefHeight(200);
        rails.translateYProperty().set(110);

        double railLength = 125; //Comprimento do trilho

        //Caminho padrao (ambos da esquerda para a direita)
        //Caminho do Trem 1:
        Path firstPosition1 = createPath(new double[]{0, -65, 0, -65, 0, 65, 0, 65, 0}, 6, 172, railLength);
        //Caminho do Trem 2:
        Path firstPosition2 = createPath(new double[]{0, 65, 0, 65, 0, -65, 0, -65, 0}, 6, 247, railLength);

        //Caminhos alternativos:
        //Ambos da direita para a esquerda
        //segundo caminho do Trem 1:
        Path secPosition1 = createPath(new double[]{0, -291, 0, -291, 0, 291, 0, 291, 0}, 695, 172, -1*railLength); 
        //segundo caminho do Trem 2:
        Path secPosition2 = createPath(new double[]{0, 291, 0, 291, 0, -291, 0, -291, 0}, 695, 247, -1*railLength); 

        //Trem 1 da esquerda para a direita e Trem 2 da direita para a esquerda
        //terceiro caminho do Trem 1:
        Path thirdPosition1 = createPath(new double[]{0, -65, 0, -65, 0, 65, 0, 65, 0}, 6, 172, railLength); 
        //terceiro caminho do Trem 2:
        Path thirdPosition2 = createPath(new double[]{0, 291, 0, 291, 0, -291, 0, -291, 0}, 695, 247, -1*railLength); 
        
        //Trem 2 da esquerda para a direita e Trem 1 da direita para a esquerda
        //quarto caminho do Trem 1:
        Path fourPosition1 = createPath(new double[]{0, -291, 0, -291, 0, 291, 0, 291, 0}, 695, 172, -1*railLength); 
        //quarto caminho do Trem 2:
        Path fourPosition2 = createPath(new double[]{0, 65, 0, 65, 0, -65, 0, -65, 0}, 6, 247, railLength); 
      /* FIM PAINEL DOS TRILHOS, E CAMINHOS */

      //Adicao dos elementos na tela principal:
      viewPane.getChildren().addAll(rails, firstPosition1, firstPosition2, train1, train2);

      /* EDIT BOTOES */
        buttonsHBox.translateXProperty().set(200);
        buttonsHBox.translateYProperty().set(45);
        buttonsHBox.setSpacing(50);

        firstBTNs.setSpacing(10);
        firstBTNs.cursorProperty().set(Cursor.HAND);
        playBTN.setStyle("-fx-background-image: url('medBtn.png'); -fx-background-position: center center;" +
                      "\n-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 12px;");
        playBTN.setPrefWidth(70);
        playBTN.setPrefHeight(40);
        resetBTN.setStyle("-fx-background-image: url('medBtn.png'); -fx-background-position: center center;" +
                      "\n-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 12px;");
        resetBTN.setPrefWidth(70);
        resetBTN.setPrefHeight(40);

        secondBTNs.setSpacing(10);
        secondBTNs.cursorProperty().set(Cursor.HAND);
        velHBox1.setSpacing(10);
        velHBox2.setSpacing(10);

        velTitle1.cursorProperty().set(Cursor.DEFAULT);
        velTitle2.cursorProperty().set(Cursor.DEFAULT);
        velTitle1.styleProperty().set("-fx-text-fill: #fff;");
        velTitle2.styleProperty().set("-fx-text-fill: #fff;");
        velValue1.cursorProperty().set(Cursor.DEFAULT);
        velValue2.cursorProperty().set(Cursor.DEFAULT);
        velValue1.styleProperty().set("-fx-text-fill: #fff;");
        velValue2.styleProperty().set("-fx-text-fill: #fff;");
        changePositionBTN.cursorProperty().set(Cursor.HAND);
        changePositionBTN.setStyle("-fx-background-image: url('bigBtn.png'); -fx-background-position: center center;" +
                      "\n-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 12px;");
        changePositionBTN.setPrefWidth(140);
        changePositionBTN.setPrefHeight(40);

        velVBox1.getChildren().addAll(velTitle1, velValue1);
        velVBox2.getChildren().addAll(velTitle2, velValue2);
        velDownBTN1.setStyle("-fx-background-image: url('smallBtn.png'); -fx-background-position: center center;" +
                      "\n-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 12px;");
        velDownBTN1.setPrefWidth(40);
        velDownBTN1.setPrefHeight(40);
        velDownBTN2.setStyle("-fx-background-image: url('smallBtn.png'); -fx-background-position: center center;" +
                      "\n-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 12px;");
        velDownBTN2.setPrefWidth(40);
        velDownBTN2.setPrefHeight(40);
        velUpBTN1.setStyle("-fx-background-image: url('smallBtn.png'); -fx-background-position: center center;" +
                      "\n-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 12px;");
        velUpBTN1.setPrefWidth(40);
        velUpBTN1.setPrefHeight(40);
        velUpBTN2.setStyle("-fx-background-image: url('smallBtn.png'); -fx-background-position: center center;" +
                      "\n-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 12px;");
        velUpBTN2.setPrefWidth(40);
        velUpBTN2.setPrefHeight(40);
      /* FIM EDIT BOTOES */

      //Adicao dos elementos aos respectivos elementos pais e aA tela raiz
      firstBTNs.getChildren().addAll(playBTN, resetBTN);

      velHBox1.getChildren().addAll(velDownBTN1, velUpBTN1, velVBox1);
      velHBox2.getChildren().addAll(velDownBTN2, velUpBTN2, velVBox2);
      secondBTNs.getChildren().addAll(velHBox1, velHBox2);

      buttonsHBox.getChildren().addAll(firstBTNs, secondBTNs, changePositionBTN);
      mainVBox.getChildren().addAll(viewPane, buttonsHBox);

      root.getChildren().addAll(mainVBox);
    /* FIM DOS ELEMENTOS DA INTERFACE */

    // INSTANCIA DAS THREADS DE CADA TREM
      TopTrain topTrain = new TopTrain(train1, firstPosition1);
      BottomTrain bottomTrain = new BottomTrain(train2, firstPosition2);
    // FIM INSTANCIA DAS THREADS DE CADA TREM

    /* EVENTOS DE CLICK */
      /* ***************************************************************
       * Evento: click
       * Funcao: inicializa/pausa a movimentacao dos trens
       * Parametros: nao recebe parametros
       * Retorno: nao retorna valores
       *************************************************************** */
      playBTN.onMouseClickedProperty().set(e -> {
        g_playClicked++;
        if (!g_isMoving) {
        //Caso os trens estiverem parados:
          g_isMoving = true; //Os trens passam a se mover
          if (g_playClicked == 1) { //1 click => start
            topTrain.start(); //Inicia a movimentacao do trem de cima
            bottomTrain.start(); //Inicia a movmentacao do trem de baixo

          } else { //mais de um click => pause/play
            topTrain.playTrain();
            bottomTrain.playTrain();
          }

          //Edit de botoes e valores da tela:
            playBTN.setText("Pause");
            velValue1.setText(String.format("%.0f Km/h vel1", topTrain.getTrainSpeed()*100));
            velValue2.setText(String.format("%.0f Km/h vel2", bottomTrain.getTrainSpeed()*200));  
          //FIM Edit de botoes e valores da tela
        } else {
        //Caso os trens ja estiverem em movimento:
          g_isMoving = false;
          //pausar as threads
          //topTrain.getTrainMovement().pause();
          //bottomTrain.getTrainMovement().pause();
          topTrain.pauseTrain();
          bottomTrain.pauseTrain();

          //Edit de botoes e valores da tela:
            playBTN.setText("Play");
            velValue1.setText(String.format("0 Km/h vel1"));
            velValue2.setText(String.format("0 Km/h vel2"));
          //FIM Edit de botoes e valores da tela
        }
      });
    
      /* ***************************************************************
       * Evento: click
       * Funcao: reinicia os trens
       * Parametros: nao recebe parametros
       * Retorno: nao retorna valores
       *************************************************************** */
      resetBTN.onMouseClickedProperty().set(e -> {
        topTrain.resetPos(g_isMoving);
        bottomTrain.resetPos(g_isMoving);
        
        if(!g_isMoving) {
          velValue1.setText(String.format("0 Km/h vel1"));
          velValue2.setText(String.format("0 Km/h vel2"));

        } else {
          velValue1.setText(String.format("%.0f Km/h vel1", topTrain.getTrainSpeed()*100));
          velValue2.setText(String.format("%.0f Km/h vel2", bottomTrain.getTrainSpeed()*200));

        }
      });

      /* ***************************************************************
       * Evento: click
       * Funcao: diminui a velocidade do trem 1
       * Parametros: nao recebe parametros
       * Retorno: nao retorna valores
       *************************************************************** */
      velDownBTN1.onMouseClickedProperty().set(e -> {
        if (topTrain.getTrainSpeed() >= 0.01 && g_isMoving) {
        //Se a velocidade estiver acima de 1 Km/h e já em movimento (playClick=1):
          //Diminuimos a velocidade em 1 Km/h a cada click e atualizamos o valor-texto da velocidade
          topTrain.setTrainSpeed(topTrain.getTrainSpeed()-0.01);
          velValue1.setText(String.format("%.0f Km/h", topTrain.getTrainSpeed()*100));
          
        } else if (!g_isMoving) {
        //Caso o trem 1 esteja parado, emitir o alerta:
          alertWindow("O trem está parado! Aumente a velocidade.");
        }
      });
      /* ***************************************************************
       * Evento: click
       * Funcao: aumenta a velocidade do trem 1
       * Parametros: nao recebe parametros
       * Retorno: nao retorna valores
       *************************************************************** */
      velUpBTN1.onMouseClickedProperty().set(e -> {
        if (topTrain.getTrainSpeed() < 0.1) {
        //Se a velocidade estiver abaixo de 1 Km/h (parado ou lento):
          //Aumentamos a velocidade em 1 Km/h a cada click e atualizamos o valor-texto da velocidade
          topTrain.setTrainSpeed(topTrain.getTrainSpeed()+0.01);
          velValue1.setText(String.format("%.0f Km/h", topTrain.getTrainSpeed()*100));
          
        } else if (!g_isMoving) {
        //Caso o trem 1 esteja parado, emitir o alerta:
          alertWindow("O trem está parado! Aperte o play para movê-lo.");
        }
      });

      /* ***************************************************************
       * Evento: click
       * Funcao: diminui a velocidade do trem 2
       * Parametros: nao recebe parametros
       * Retorno: nao retorna valores
       *************************************************************** */
      velDownBTN2.onMouseClickedProperty().set(e -> {
        if (bottomTrain.getTrainSpeed() >= 0.01 && g_isMoving) {
        //Se a velocidade estiver acima de 1 Km/h (em movimento):
          //Diminuimos a velocidade em 1 Km/h a cada click e atualizamos o valor-texto da velocidade
          bottomTrain.setTrainSpeed(bottomTrain.getTrainSpeed()-0.01);
          velValue2.setText(String.format("%.0f Km/h", bottomTrain.getTrainSpeed()*100));
        } else if (!g_isMoving) {
        //Caso o trem 1 esteja parado, emitir o alerta:
          alertWindow("O trem está parado! Aumente a velocidade.");
        }
      });
      /* ***************************************************************
       * Evento: click
       * Funcao: aumenta a velocidade do trem 2
       * Parametros: nao recebe parametros
       * Retorno: nao retorna valores
       *************************************************************** */
      velUpBTN2.onMouseClickedProperty().set(e -> {
        if (bottomTrain.getTrainSpeed() < 0.1) {
        //Se a velocidade estiver abaixo de 1 Km/h (parado ou lento):
          //Aumentamos a velocidade em 1 Km/h a cada click e atualizamos o valor-texto da velocidade
          bottomTrain.setTrainSpeed(bottomTrain.getTrainSpeed()+0.01);
          velValue2.setText(String.format("%.0f Km/h", bottomTrain.getTrainSpeed()*100));
          
        } else if (!g_isMoving) {
        //Caso o trem 2 esteja parado, emitir o alerta:
          alertWindow("O trem está parado!\nAperte o play para movê-lo.");
        }
      });

      /* ***************************************************************
       * Evento: click
       * Funcao: troca a posicao dos trens
       * Parametros: nao recebe parametros
       * Retorno: nao retorna valores
       *************************************************************** */
      changePositionBTN.onMouseClickedProperty().set(e -> {
        if (g_isMoving) {
        //Se os trens estiverem se movendo, ao mudar de posicao, o botao de play/pause tambem reseta para o estado inicial (play) e os trens param de andar
          playBTN.setText("Play");
          g_isMoving = false;
          topTrain.pauseTrain();
          bottomTrain.pauseTrain();
        }

        //Reseta o valor-velocidade dos trens na GUI
        velValue1.setText("0 Km/h");
        velValue2.setText("0 Km/h");

        if (topTrain.getPath().equals(firstPosition1) && bottomTrain.getPath().equals(firstPosition2)) {
        //Caso os trens estejam no primeiro caminho respectivamente (firsPosition1/2):
          //Reinicializamos a animacao para o momento zero e paramos a animacao
          resetBTN.fireEvent(e);
          
          //Mudamos a posicao dos trens para a posicao do segundo caminho (secPosition1/2) e definimos este novo caminho
          train1.translateXProperty().set(680);
          train1.setTranslateY(162);
          train2.setTranslateX(680);
          train2.setTranslateY(235);
          topTrain.setNewPath(secPosition1);
          bottomTrain.setNewPath(secPosition2);
          //E tambem definimos a velocidade dos trens para 0.10 ("10 Km/h")
          topTrain.setTrainSpeed(0.1);
          bottomTrain.setTrainSpeed(0.1);

        } else if (topTrain.getPath().equals(secPosition1) && bottomTrain.getPath().equals(secPosition2)) {
        //Caso os trens estejam no segundo caminho respectivamente (secPosition1/2):
          //Reiniciaizamos a animacao para o momento zero e paramos a animacao
          resetBTN.fireEvent(e);

          //Mudamos a posicao dos trens para a posicao do terceiro caminho (thirdPosition1/2) e definimos este novo caminho
          train1.setTranslateX(0);
          train1.setTranslateY(162);
          train2.setTranslateX(680);
          train2.setTranslateY(235);
          topTrain.setNewPath(thirdPosition1);
          bottomTrain.setNewPath(thirdPosition2);
          //E tambem definimos a velocidade dos trens para 0.10 ("10 Km/h")
          topTrain.setTrainSpeed(0.1);
          bottomTrain.setTrainSpeed(0.1);

        } else if (topTrain.getPath().equals(thirdPosition1) && bottomTrain.getPath().equals(thirdPosition2)) {
        //Caso os trens estejam no terceiro caminho respectivamente (thirdPosition1/2):
          //Reiniciaizamos a animacao para o momento zero e paramos a animacao
          resetBTN.fireEvent(e);

          //Mudamos a posicao dos trens para a posicao do quarto caminho (fourPosition1/2) e definimos este novo caminho
          train1.setTranslateX(680);
          train1.setTranslateY(162);
          train2.setTranslateX(0);
          train2.setTranslateY(235);
          topTrain.setNewPath(fourPosition1);
          bottomTrain.setNewPath(fourPosition2);
          //E tambem definimos a velocidade dos trens para 0.10 ("10 Km/h")
          topTrain.setTrainSpeed(0.1);
          bottomTrain.setTrainSpeed(0.1);
        } else {
        //Caso os trens estejam no quarto caminho respectivamente (fourPosition1/2):
          //Reiniciaizamos a animacao para o momento zero e paramos a animacao
          resetBTN.fireEvent(e);

          //Mudamos a posicao dos trens para a posicao do primeiro caminho (firsPosition1/2) e definimos este novo caminho
          train1.setTranslateX(0);
          train1.setTranslateY(162);
          train2.setTranslateX(0);
          train2.setTranslateY(235);
          topTrain.setNewPath(firstPosition1);
          bottomTrain.setNewPath(firstPosition2);
          //E tambem definimos a velocidade dos trens para 0.10 ("10 Km/h")
          topTrain.setTrainSpeed(0.1);
          bottomTrain.setTrainSpeed(0.1);
        }
      });
    /* FIM EVENTOS DE CLICK */

    primaryStage.show(); //Exibe a janela principal e inicia a aplicacao
  }

  /** *************************************************************
   * Metodo: createPath
   * Funcao: cria um caminho para o trem percorrer
   * Parametros: array de angulos, valor do eixo x, valor do eixo y, comprimento do caminho
   * Retorno: nao retorna valores
   ***************************************************************
   * @param angles sequencia de angulos sobre os quais a linha do caminho vai girar para formar ou nao uma curva
   * @param x coordenada x do ponto inicial do caminho
   * @param y coordenada y do ponto inicial do caminho
   * @param length tamanho total do caminho
   * @return o caminho completo para o trem percorrer
   */
  private Path createPath(double[] angles, double x, double y, double length) {
    Path path = new Path();
    path.setStroke(Color.rgb(255, 255, 0, 1)); //Cor da linha do caminho
    path.getElements().add(new MoveTo(x, y)); //Define o ponto onde o caminho comeca

    for(double angle : angles){
    //Percorre um array com X angulos -- A quantidde X de vezes que o trem fara uma curva
      if(angle != 0) { 
      /* Quando o angulo (em relacao ao eixo X) for diferente de 0:
       * o trem faz uma curva para cima quando o angulo for positivo
       * o trem faz uma curva para baixo quando o angulo for negativo
       * incrementamos, a cada coordenada, o valor total do tamanho do trilho vezes o seno ou cosseno
       * aqui, em ambos os casos, dividimos o tamanho do trilho total por tres para que este seja o tamanho da reta apos o ponto de curva
       */
        x += length/3 * Math.cos(Math.toRadians(angle)); 
        y -= length/3 * Math.sin(Math.toRadians(angle));
      } else {
      //O trem se mantem em linha reta quando o angulo for 0 e incrementamos o valor total do tamanho do trilho vezes o seno ou cosseno
        x += length * Math.cos(Math.toRadians(angle));
        y -= length * Math.sin(Math.toRadians(angle));
      }
      /* 
       * [!] relembrando o incremento dado a cada coordenada
       * Apos cada iteracao, forma-se uma linha ate o ponto (x,y)
       * 
       */
      path.getElements().add(new LineTo(x, y));
    }

    return path; //Retorna o caminho completo para o trem percorrer
  }

  /** ***************************************************************
   * Metodo: alertWindow
   * Funcao: cria uma janela de alerta
   * Parametros: mensagem de alerta
   * Retorno: nao retorna valores
   ***************************************************************
   * @param msg mensagem de alerta
   * @return nao retorna valores
   */
  public void alertWindow(String msg) {
    //Instacia-se a janela de alerta
    Stage alertStage = new Stage();
    Pane alert = new Pane();
    Scene alertWindow = new Scene(alert, 300, 160);

    //Definimos o titulo e outros detalhes da janela
    alertStage.setTitle("Aviso!");
    alertStage.setScene(alertWindow);
    alertStage.setResizable(false);
    alertStage.centerOnScreen();
    alertStage.initModality(Modality.APPLICATION_MODAL);

    VBox alertVBox = new VBox();
    alertVBox.setAlignment(Pos.CENTER); //posicionada no centro
      Label alertMsg = new Label(msg); //mensagem de alerta
      alertMsg.setStyle("-fx-text-fill: #fff");
      Button alertBTN = new Button("OK"); //botao de fechar a janela
      alertBTN.cursorProperty().set(Cursor.HAND); 
      alertBTN.setPrefWidth(40);
      alertBTN.setPrefHeight(40);
    //Fim alertVBox

    /* EDIT alertVBox */
      alertVBox.setStyle("-fx-background-image: url(tela-fundo.jpg);-fx-pref-width: 300px;-fx-pref-height: 160px;" + 
                        "\n-fx-padding: 5px;-fx-text-align: center;-fx-font-size: 14px;-fx-font-weight: bold;");

      alertBTN.setStyle("-fx-background-image: url('smallBtn.png'); -fx-background-position: center center;" +
                        "\n-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 12px;");
      alertBTN.setTranslateY(20);
    /* FIM EDIT alertVBox */
    
    //Adicionamos os elementos aA janela de alerta
    alertVBox.getChildren().addAll(alertMsg, alertBTN);
    alert.getChildren().addAll(alertVBox);

    //Ao clicar no botao, a janela de alerta fecha
    alertBTN.onMouseClickedProperty().set(e -> {
      alertStage.close();
    });

    //Iniciamos a janela de alerta
    alertStage.getOnCloseRequest();
    alertStage.show();
  }
	
	public static void main(String[] args) {
		launch(args);
	}
}