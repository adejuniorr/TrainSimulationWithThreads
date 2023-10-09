/* ***************************************************************
* Autor............: Ademir de Jesus Reis Junior
* Matricula........: 202210327
* Inicio...........: 19/09/2023
* Ultima alteracao.: 08/10/2023
* Nome.............: Principal.java
* Funcao...........: Aplicacao JavaFX que simula trens em uma linha ferroviaria com o uso de Threads
*************************************************************** */

// Importacao de bibliotecas
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
// Fim importacao de bibliotecas

public class Principal extends Application { // Aplicacao JavaFX

  private boolean g_isMoving = false; // Variavel global que define se os trens estao em movimento ou nao
  private int g_playClicked = 0; // Varivavel global que checa os clicks no botao de play para dar start na thread ou play/pause na animacao da mesma

  public static int g_mutualExclusion = 0; // Variavel global que define o metodo de exclusao mutua a ser utilizado para evitar colisoes

  public static int lock1 = 0; // Variavel de travamento para acesso aA primeira regiao critica
  public static int lock2 = 0; // Variavel de travamento para acesso aA segunda regiao critica

  public static int turn1 = 0; // Variavel de controle de vez para acesso aA primeira regiao critica por Estrita Alternancia
  public static int turn2 = 0; // Variavel de controle de vez para acesso aA segunda regiao critica por Estrita Alternancia

  public static int turnRC1; // Variavel de controle de vez para acesso aA primeira regiao critica por Peterson
  public static int turnRC2; // Variavel de controle de vez para acesso aA segunda regiao critica por Peterson
  public static boolean[] flagRC1 = new boolean[2]; // Vetor de flags para acesso aA primeira regiao critica por Peterson
  public static boolean[] flagRC2 = new boolean[2]; // Vetor de flags para acesso aA segunda regiao critica por Peterson

  /**
   * *************************************************************
   * Metodo: start
   * Funcao: inicializa a aplicacao JavaFX
   * Parametros: Stage primaryStage
   * Retorno: nao retorna valores
   ***************************************************************
   * @param primaryStage
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    /* PAINEL RAIZ */
    Pane root = new Pane();
    root.styleProperty().set("-fx-background-image: url('img/tela-fundo.jpg');"); // Imagem de fundo

    Scene scene = new Scene(root, 1000, 600); // Tamanho da cena principal

    primaryStage.setTitle("A Vida é Trem Bala Parcero"); // Titulo da cena principal
    primaryStage.setScene(scene); // Adiciona a cena na janela
    primaryStage.setResizable(false); // Tamanho nao alteravel
    primaryStage.centerOnScreen(); // Janela centralizada no monitor
    /* FIM PAINEL RAIZ */

    /* ESTRUTURA DA INTERFACE */
    VBox mainVBox = new VBox(); // Painel principal
    Pane viewPane = new Pane(); // Painel de vizualizacao dos trens e trilhos
    HBox buttonsHBox = new HBox(); // Painel de botoes
    Label title = new Label("A Vida é Trem Bala Parcero"); // Titulo da aplicacao

    VBox firstBTNs = new VBox(); // Botoes play/pause e reset
    HBox playPauseBTNs = new HBox(); // Botoes play/pause e reset
    Button playBTN = new Button("Play"); // Botao que inicia/pausa a movimentacao dos trens
    Button resetBTN = new Button("Reset"); // Botao que reinicia a posicao dos trens
    Button changePositionBTN = new Button("Trocar Posições"); // Botao que troca a posicao dos trens
    // end firstBTNs - play/pause, reset e changePosition

    VBox secondBTNs = new VBox(); // Botoes de velocidade

    HBox velHBox1 = new HBox(); // Botoes de velocidade do Trem 1
    Button velDownBTN1 = new Button("-"); // Botao que diminui a velocidade do trem 1
    Button velUpBTN1 = new Button("+"); // Botao que aumenta a velocidade do trem 1
    VBox velVBox1 = new VBox(); // Painel de titulo e valor da velocidade do trem 1
    Label velTitle1 = new Label("Velocidade do Trem 1"); // Titulo da velocidade do trem 1
    Label velValue1 = new Label("0 Km/h"); // Valor da velocidade do trem 1
    // end velHBox1 - velocidade do trem 1

    HBox velHBox2 = new HBox(); // Botoes de velocidade do Trem 2
    Button velDownBTN2 = new Button("-"); // Botao que diminui a velocidade do trem 2
    Button velUpBTN2 = new Button("+"); // Botao que aumenta a velocidade do trem 2
    VBox velVBox2 = new VBox(); // Painel de titulo e valor da velocidade do trem 2
    Label velTitle2 = new Label("Velocidade do Trem 2"); // Titulo da velocidade do trem 2
    Label velValue2 = new Label("0 Km/h"); // Valor da velocidade do trem 2
    // end velHBox2 - velocidade do trem 2
    // end secondBTNs - botoes de velocidade

    VBox thirdBTNs = new VBox(); // Botao de troca de posicao e metodos de acesso a regiao critica
    Label methodsTitle = new Label("Selecionar Exclusão Mútua"); // Titulo dos metodos de acesso a regiao critica

    HBox methodsBTNs = new HBox(); // Painel de botoes de metodos de acesso a regiao critica
    Button lockVarBTN = new Button("1"); // Metodo com variavel de travamento
    Button strictAltBTN = new Button("2"); // Metodo com alternancia estrita
    Button petersonBTN = new Button("3"); // Metodo de Peterson
    Button noneExcBTN = new Button("X"); // Botao para remover a exclusao mutua (permitir colisoes)
    // end buttonsHBox - painel de botoes
    // end mainVBox
    /* FIM DA ESTRUTURA DA INTERFACE */

    /* ELEMENTOS DA INTERFACE */
    /* TELA PRINCIPAL */
    // dimensoes:
    viewPane.setPrefWidth(700);
    viewPane.setPrefHeight(400);
    // posicionamento:
    viewPane.translateXProperty().set(150);
    viewPane.translateYProperty().set(25);
    // imagem de fundo:
    viewPane.styleProperty().set("-fx-background-image: url('img/background2.png');");
    /* FIM TELA PRINCIPAL */

    /* PAINEIS DOS TRENS */
    Pane train1 = new Pane();
    train1.styleProperty().set("-fx-background-image: url('img/train-img-20x20.png')");
    train1.setPrefWidth(20);
    train1.setPrefHeight(20);
    train1.translateXProperty().set(0);
    train1.translateYProperty().set(162);

    Pane train2 = new Pane();
    train2.styleProperty().set("-fx-background-image: url('img/train-img-20x20.png')");
    train2.setPrefWidth(20);
    train2.setPrefHeight(20);
    train2.translateXProperty().set(0);
    train2.translateYProperty().set(235);
    /* FIM PAINEIS DOS TRENS */

    /* PAINEL DOS TRILHOS, E CAMINHOS */
    Pane rails = new Pane(); // Painel que contem os trilhos (imagem.png)
    rails.styleProperty().set(
        "-fx-background-image: url('img/main-rails.png');\n-fx-background-position: center center;\n-fx-background-repeat: no-repeat;\n-fx-background-size: 100%;"); // Imagem dos trilhos
    rails.setPrefWidth(700); // Largura
    rails.setPrefHeight(200); // Altura
    rails.translateYProperty().set(110); // Posicionamento

    // Criacao dos caminhos dos trens:
    double railLength = 125; // Comprimento do caminho/trilho

    // Caminho padrao (ambos da esquerda para a direita)
    // Caminho do Trem 1:
    Path firstPosition1 = createPath(new double[] { 0, -65, 0, -65, 0, 65, 0, 65, 0 }, 6, 172, railLength);
    // Caminho do Trem 2:
    Path firstPosition2 = createPath(new double[] { 0, 65, 0, 65, 0, -65, 0, -65, 0 }, 6, 247, railLength);

    // Caminhos alternativos:
    // Ambos da direita para a esquerda
    // segundo caminho do Trem 1:
    Path secPosition1 = createPath(new double[] { 0, -291, 0, -291, 0, 291, 0, 291, 0 }, 695, 172, -1 * railLength);
    // segundo caminho do Trem 2:
    Path secPosition2 = createPath(new double[] { 0, 291, 0, 291, 0, -291, 0, -291, 0 }, 695, 247, -1 * railLength);

    // Trem 1 da esquerda para a direita e Trem 2 da direita para a esquerda
    // terceiro caminho do Trem 1:
    Path thirdPosition1 = createPath(new double[] { 0, -65, 0, -65, 0, 65, 0, 65, 0 }, 6, 172, railLength);
    // terceiro caminho do Trem 2:
    Path thirdPosition2 = createPath(new double[] { 0, 291, 0, 291, 0, -291, 0, -291, 0 }, 695, 247, -1 * railLength);

    // Trem 2 da esquerda para a direita e Trem 1 da direita para a esquerda
    // quarto caminho do Trem 1:
    Path fourPosition1 = createPath(new double[] { 0, -291, 0, -291, 0, 291, 0, 291, 0 }, 695, 172, -1 * railLength);
    // quarto caminho do Trem 2:
    Path fourPosition2 = createPath(new double[] { 0, 65, 0, 65, 0, -65, 0, -65, 0 }, 6, 247, railLength);
    /* FIM PAINEL DOS TRILHOS, E CAMINHOS */

    // Adicao dos elementos na tela principal:
    viewPane.getChildren().addAll(rails, firstPosition1, firstPosition2, train1, train2, title);
    title.setStyle("-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 20px;"); // Estilo do titulo da aplicacao
    title.translateXProperty().set(230); // Posicionamento X do titulo da aplicacao

    /* EDIT BOTOES */
    buttonsHBox.translateXProperty().set(190); // Posicionamento X do painel de botoes principal
    buttonsHBox.translateYProperty().set(45); // Posicionamento Y do painel de botoes principal
    buttonsHBox.setSpacing(20); // Espacamento entre os paineis de botoes dentro do painel principal

    // Inicio Edit primeiro painel de botoes
    firstBTNs.setSpacing(10); // Espacamento entre os botoes do primeiro painel de botoes
    firstBTNs.setAlignment(Pos.CENTER); // Alinhamento dos botoes do primeiro painel de botoes ao centro
    // Edit do botao play/pause
    playPauseBTNs.setSpacing(10); // Espacamento entre os botoes de play/pause e reset
    playBTN.setStyle("-fx-background-image: url('img/medBtn.png'); -fx-background-position: center center;" +
        "\n-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 12px; -fx-border: none;"); // Estilo do botao de play/pause
    playBTN.cursorProperty().set(Cursor.HAND); // Cursor de mao ao passar o mouse por cima do botao
    playBTN.setPrefWidth(70); // Largura do botao de play/pause
    playBTN.setPrefHeight(40); // Altura do botao de play/pause
    // Fim Edit do botao play/pause

    // Inicio Edit do botao de reset
    resetBTN.setStyle("-fx-background-image: url('img/medBtn.png'); -fx-background-position: center center;" +
        "\n-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 12px; -fx-border: none;"); // Estilo do botao de reset
    resetBTN.cursorProperty().set(Cursor.HAND); // Cursor de mao ao passar o mouse por cima do botao
    resetBTN.setPrefWidth(70); // Largura do botao de reset
    resetBTN.setPrefHeight(40); // Altura do botao de reset
    // Fim Edit do botao de reset

    // Inicio Edit do botao de troca de posicao
    changePositionBTN.cursorProperty().set(Cursor.HAND); // Cursor de mao ao passar o mouse por cima do botao de troca de posicao
    changePositionBTN.setStyle("-fx-background-image: url('img/bigBtn.png'); -fx-background-position: center center;" +
        "\n-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 12px; -fx-border: none;"); // Estilo do botao de troca de posicao
    changePositionBTN.setPrefWidth(150); // Largura do botao de troca de posicao
    changePositionBTN.setPrefHeight(40); // Altura do botao de troca de posicao
    // Fim Edit do botao de troca de posicao
    // Fim Edit primeiro painel de botoes

    // Inicio Edit segundo painel de botoes
    secondBTNs.setSpacing(10); // Espacamento entre os botoes do segundo painel de botoes
    velHBox1.setSpacing(10); // Espacamento entre os botoes de velocidade do trem 1
    velHBox2.setSpacing(10); // Espacamento entre os botoes de velocidade do trem 2

    velTitle1.styleProperty().set("-fx-text-fill: #fff; -fx-font-weight: bold;"); // Estilo do titulo da velocidade do trem 1
    velTitle2.styleProperty().set("-fx-text-fill: #fff; -fx-font-weight: bold;"); // Estilo do titulo da velocidade do trem 2
    velValue1.styleProperty().set("-fx-text-fill: #fff; -fx-font-weight: bold;"); // Estilo do valor da velocidade do trem 1
    velValue2.styleProperty().set("-fx-text-fill: #fff; -fx-font-weight: bold;"); // Estilo do valor da velocidade do trem 2

    velVBox1.getChildren().addAll(velTitle1, velValue1); // Adicao dos elementos ao painel de titulo e valor da velocidade do trem 1
    velVBox2.getChildren().addAll(velTitle2, velValue2); // Adicao dos elementos ao painel de titulo e valor da velocidade do trem 2

    velDownBTN1.setStyle("-fx-background-image: url('img/smallBtn.png'); -fx-background-position: center center;" +
        "\n-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 12px; -fx-border: none;");
    velDownBTN1.cursorProperty().set(Cursor.HAND); // Cursor de mao ao passar o mouse por cima do botao de diminuir a velocidade do trem 1
    velDownBTN1.setPrefWidth(40); // Largura do botao de diminuir a velocidade do trem 1
    velDownBTN1.setPrefHeight(40); // Altura do botao de diminuir a velocidade do trem 1

    velDownBTN2.setStyle("-fx-background-image: url('img/smallBtn.png'); -fx-background-position: center center;" +
        "\n-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 12px; -fx-border: none;");
    velDownBTN2.cursorProperty().set(Cursor.HAND); // Cursor de mao ao passar o mouse por cima do botao de diminuir a velocidade do trem 2
    velDownBTN2.setPrefWidth(40); // Largura do botao de diminuir a velocidade do trem 2
    velDownBTN2.setPrefHeight(40); // Altura do botao de diminuir a velocidade do trem 2

    velUpBTN1.setStyle("-fx-background-image: url('img/smallBtn.png'); -fx-background-position: center center;" +
        "\n-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 12px; -fx-border: none;"); // Estilo do botao de aumentar a velocidade do trem 1
    velUpBTN1.cursorProperty().set(Cursor.HAND); // Cursor de mao ao passar o mouse por cima do botao de aumentar a velocidade do trem 1
    velUpBTN1.setPrefWidth(40); // Largura do botao de aumentar a velocidade do trem 1
    velUpBTN1.setPrefHeight(40); // Altura do botao de aumentar a velocidade do trem 1

    velUpBTN2.setStyle("-fx-background-image: url('img/smallBtn.png'); -fx-background-position: center center;" +
        "\n-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 12px; -fx-border: none;"); // Estilo do botao de aumentar a velocidade do trem 2
    velUpBTN2.cursorProperty().set(Cursor.HAND); // Cursor de mao ao passar o mouse por cima do botao de aumentar a velocidade do trem 2
    velUpBTN2.setPrefWidth(40); // Largura do botao de aumentar a velocidade do trem 2
    velUpBTN2.setPrefHeight(40); // Altura do botao de aumentar a velocidade do trem 2
    // Fim Edit segundo painel de botoes

    // Inicio Edit terceiro painel de botoes
    thirdBTNs.setSpacing(10); // Espacamento entre os botoes do terceiro painel de botoes
    thirdBTNs.alignmentProperty().set(Pos.CENTER); // Alinhamento dos botoes do terceiro painel de botoes ao centro

    methodsBTNs.setSpacing(10); // Espacamento entre os botoes de metodos de acesso a regiao critica
    methodsTitle.styleProperty().set("-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 14px;"); // Estilo do titulo dos metodos de acesso a regiao critica

    lockVarBTN.cursorProperty().set(Cursor.HAND); // Cursor de mao ao passar o mouse por cima do botao de variavel de travamento
    lockVarBTN.setPrefWidth(40); // Largura do botao de variavel de travamento
    lockVarBTN.setPrefHeight(40); // Altura do botao de variavel de travamento
    lockVarBTN.setStyle("-fx-background-image: url('img/smallBtn.png'); -fx-background-position: center center;" +
        "\n-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 12px; -fx-border: none;"); // Estilo do botao de variavel de travamento

    strictAltBTN.cursorProperty().set(Cursor.HAND); // Cursor de mao ao passar o mouse por cima do botao de alternancia estrita
    strictAltBTN.setPrefWidth(40); // Largura do botao de alternancia estrita
    strictAltBTN.setPrefHeight(40); // Altura do botao de alternancia estrita
    strictAltBTN.setStyle("-fx-background-image: url('img/smallBtn.png'); -fx-background-position: center center;" +
        "\n-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 12px; -fx-border: none;"); // Estilo do botao de alternancia estrita

    petersonBTN.cursorProperty().set(Cursor.HAND); // Cursor de mao ao passar o mouse por cima do botao de peterson
    petersonBTN.setPrefWidth(40); // Largura do botao de peterson
    petersonBTN.setPrefHeight(40); // Altura do botao de peterson
    petersonBTN.setStyle("-fx-background-image: url('img/smallBtn.png'); -fx-background-position: center center;" +
        "\n-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 12px; -fx-border: none;"); // Estilo do botao de peterson

    noneExcBTN.cursorProperty().set(Cursor.HAND); // Cursor de mao ao passar o mouse por cima do botao de remover a exclusao mutua
    noneExcBTN.setPrefWidth(40); // Largura do botao de remover a exclusao mutua
    noneExcBTN.setPrefHeight(40); // Altura do botao de remover a exclusao mutua
    noneExcBTN.setStyle("-fx-background-image: url('img/smallBtn.png'); -fx-background-position: center center;" +
        "\n-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 12px; -fx-border: none;"); // Estilo do botao de remover a exclusao mutua
    // Fim Edit terceiro painel de botoes
    /* FIM EDIT BOTOES */

    // Adicao dos elementos aos respectivos elementos pais e aA tela raiz
    playPauseBTNs.getChildren().addAll(playBTN, resetBTN); // Adicao dos botoes de play/pause e reset ao painel de botoes de play/pause e reset
    firstBTNs.getChildren().addAll(playPauseBTNs, changePositionBTN); // Adicao dos paineis de botoes play/pause e reset e do painel do botao de troca de posicao ao painel de botoes principal

    velHBox1.getChildren().addAll(velDownBTN1, velUpBTN1, velVBox1); // Adicao dos botoes de velocidade do trem 1 e do painel de titulo e valor da velocidade do trem 1 ao painel de botoes de velocidade do trem 1
    velHBox2.getChildren().addAll(velDownBTN2, velUpBTN2, velVBox2); // Adicao dos botoes de velocidade do trem 2 e do painel de titulo e valor da velocidade do trem 2 ao painel de botoes de velocidade do trem 2
    secondBTNs.getChildren().addAll(velHBox1, velHBox2); // Adicao dos paineis de botoes de velocidade do trem 1 e do trem 2 ao painel de botoes principal

    methodsBTNs.getChildren().addAll(lockVarBTN, strictAltBTN, petersonBTN, noneExcBTN); // Adicao dos botoes de metodos de acesso a regiao critica ao painel de botoes de metodos de acesso a regiao critica
    thirdBTNs.getChildren().addAll(methodsTitle, methodsBTNs); // Adicao do titulo dos metodos de acesso a regiao critica e do painel de botoes de metodos de acesso a regiao critica ao painel de botoes principal

    buttonsHBox.getChildren().addAll(firstBTNs, secondBTNs, thirdBTNs); // Adicao dos paineis de botoes ao painel principal

    mainVBox.getChildren().addAll(viewPane, buttonsHBox); // Adicao dos paineis de botoes e da tela principal ao painel principal

    root.getChildren().addAll(mainVBox); // Adicao do painel principal aA tela raiz
    /* FIM DOS ELEMENTOS DA INTERFACE */

    // INSTANCIA DAS THREADS DE CADA TREM
    firstPosition1.setStroke(Color.rgb(0, 255, 0, 0)); // Cor da linha do caminho
    firstPosition2.setStroke(Color.rgb(0, 0, 255, 0)); // Cor da linha do caminho
    Train topTrain = new Train(train1, firstPosition1); // Instancia da thread do trem 1
    Train bottomTrain = new Train(train2, firstPosition2); // Instancia da thread do trem 2
    // FIM INSTANCIA DAS THREADS DE CADA TREM

    /* EVENTOS DE CLICK */
    /*
     * ***************************************************************
     * Evento: click
     * Funcao: inicializa/pausa a movimentacao dos trens
     * Parametros: nao recebe parametros
     * Retorno: nao retorna valores
     */
    playBTN.onMouseClickedProperty().set(e -> { // A cada click no botao de play/pause
      g_playClicked++; // Contador de clicks no botao de play/pause eh incrementado
      if (!g_isMoving) { // Caso os trens estiverem parados:
        g_isMoving = true; // Os trens passam a se mover
        if (g_playClicked == 1) { // 1 click => start
          topTrain.start(); // Starta a thread
          bottomTrain.start(); // Starta a thread
          topTrain.playTrain(); // Play na animacao
          bottomTrain.playTrain(); // Play na animacao

        } else { // mais de um click => pause/play
          topTrain.playTrain();
          bottomTrain.playTrain();
        } // FIM if-else

        // Edit de botoes e valores da tela:
        playBTN.setText("Pause"); // Texto do botao de play/pause
        velValue1.setText(String.format("%.0f Km/h", topTrain.getTrainSpeed() * 100)); // Valor da velocidade do trem 1
        velValue2.setText(String.format("%.0f Km/h", bottomTrain.getTrainSpeed() * 100)); // Valor da velocidade do trem 2
        // FIM Edit de botoes e valores da tela
      } else { // Caso os trens ja estiverem em movimento:
        g_isMoving = false; // Os trens param de se mover
        topTrain.pauseTrain(); // Pausa na animacao do trem 1
        bottomTrain.pauseTrain(); // Pausa na animacao do trem 2

        // Edit de botoes e valores da tela:
        playBTN.setText("Play"); // Texto do botao de play/pause
        velValue1.setText(String.format("0 Km/h")); // Valor da velocidade do trem 1
        velValue2.setText(String.format("0 Km/h")); // Valor da velocidade do trem 2
        // FIM Edit de botoes e valores da tela
      } // FIM if-else
    }); // FIM Evento: click - play/pause

    /*
     * ***************************************************************
     * Evento: click
     * Funcao: reinicia os trens
     * Parametros: nao recebe parametros
     * Retorno: nao retorna valores
     */
    resetBTN.onMouseClickedProperty().set(e -> { // A cada click no botao de reset
      topTrain.resetPos(g_isMoving); // Reseta a posicao do trem 1
      bottomTrain.resetPos(g_isMoving); // Reseta a posicao do trem 2

      if (!g_isMoving) { // Caso os trens estiverem parados:
        velValue1.setText(String.format("0 Km/h")); // Valor da velocidade do trem 1
        velValue2.setText(String.format("0 Km/h")); // Valor da velocidade do trem 2

      } else { // Caso os trens estiverem em movimento:
        velValue1.setText(String.format("%.0f Km/h", topTrain.getTrainSpeed() * 100)); // Valor da velocidade do trem 1
        velValue2.setText(String.format("%.0f Km/h", bottomTrain.getTrainSpeed() * 100)); // Valor da velocidade do trem 2

      } // FIM if-else
    }); // FIM Evento: click - reset

    /*
     * ***************************************************************
     * Evento: click
     * Funcao: diminui a velocidade do trem 1
     * Parametros: nao recebe parametros
     * Retorno: nao retorna valores
     */
    velDownBTN1.setOnAction(e -> { // A cada click no botao de diminuir a velocidade do trem 1
      if (topTrain.getTrainSpeed() >= 0.01 && g_isMoving) { // Se a velocidade estiver acima de 1 Km/h e já em movimento (playClick=1)
        topTrain.setTrainSpeed(topTrain.getTrainSpeed() - 0.01); // Diminuimos a velocidade em 1 Km/h a cada click
        velValue1.setText(String.format("%.0f Km/h", topTrain.getTrainSpeed() * 100)); // Atualizamos o valor-texto da velocidade

      } else if (!g_isMoving) {  // Caso o trem 1 esteja parado
        alertWindow("O trem está parado! \nAumente a velocidade."); // Emitir o alerta de trem parado
      } // FIM if-else
    }); // FIM Evento: click - diminuir velocidade do trem 1

    /*
     * ***************************************************************
     * Evento: click
     * Funcao: aumenta a velocidade do trem 1
     * Parametros: nao recebe parametros
     * Retorno: nao retorna valores
     */
    velUpBTN1.onMouseClickedProperty().set(e -> { // A cada click no botao de aumentar a velocidade do trem 1
      if (topTrain.getTrainSpeed() < 0.1) { // Se a velocidade estiver abaixo de 1 Km/h (parado ou lento):
        topTrain.setTrainSpeed(topTrain.getTrainSpeed() + 0.01); // Aumentamos a velocidade em 1 Km/h a cada click
        velValue1.setText(String.format("%.0f Km/h", topTrain.getTrainSpeed() * 100)); // Atualizamos o valor-texto da velocidade

        if (topTrain.getTrainSpeed() == 0.01) { // Se a velocidade for 0.01 Km/h (parado):
          topTrain.playTrain(); // Play na animacao do trem 1
        } // FIM if

      } else if (!g_isMoving) { // Caso o trem 1 esteja parado
        alertWindow("\tO trem está parado! \nAperte o play para movê-lo."); // Emitir o alerta de trem parado
      } // FIM if-else
    }); // FIM Evento: click - aumentar velocidade do trem 1

    /*
     * ***************************************************************
     * Evento: click
     * Funcao: diminui a velocidade do trem 2
     * Parametros: nao recebe parametros
     * Retorno: nao retorna valores
     */
    velDownBTN2.onMouseClickedProperty().set(e -> { // A cada click no botao de diminuir a velocidade do trem 2
      if (bottomTrain.getTrainSpeed() >= 0.01 && g_isMoving) { // Se a velocidade estiver acima de 1 Km/h e já em movimento (playClick=1)
        bottomTrain.setTrainSpeed(bottomTrain.getTrainSpeed() - 0.01); // Diminuimos a velocidade em 1 Km/h a cada click
        velValue2.setText(String.format("%.0f Km/h", bottomTrain.getTrainSpeed() * 100)); // Atualizamos o valor-texto da velocidade
      } else if (!g_isMoving) { // Caso o trem 2 esteja parado
        alertWindow("O trem está parado! \nAumente a velocidade."); // Emitir o alerta de trem parado
      } // FIM if-else
    }); // FIM Evento: click - diminuir velocidade do trem 2

    /*
     * ***************************************************************
     * Evento: click
     * Funcao: aumenta a velocidade do trem 2
     * Parametros: nao recebe parametros
     * Retorno: nao retorna valores
     */
    velUpBTN2.onMouseClickedProperty().set(e -> { // A cada click no botao de aumentar a velocidade do trem 2
      if (bottomTrain.getTrainSpeed() < 0.1) { // Se a velocidade estiver abaixo de 1 Km/h (parado ou lento):
        bottomTrain.setTrainSpeed(bottomTrain.getTrainSpeed() + 0.01); // Aumentamos a velocidade em 1 Km/h a cada click
        velValue2.setText(String.format("%.0f Km/h", bottomTrain.getTrainSpeed() * 100)); // Atualizamos o valor-texto da velocidade

      } else if (!g_isMoving) { // Caso o trem 2 esteja parado
        alertWindow("\tO trem está parado!\nAperte o play para movê-lo."); // Emitir o alerta de trem parado
      } // FIM if-else
    }); // FIM Evento: click - aumentar velocidade do trem 2

    /*
     * ***************************************************************
     * Evento: click
     * Funcao: troca a posicao dos trens
     * Parametros: nao recebe parametros
     * Retorno: nao retorna valores
     */
    changePositionBTN.onMouseClickedProperty().set(e -> { // A cada click no botao de troca de posicao
      if (g_isMoving) { // Caso os trens estiverem em movimento:
        playBTN.setText("Play"); // Texto do botao de play/pause
        g_isMoving = false; // Os trens param de se mover
        topTrain.pauseTrain(); // Pausa na animacao do trem 1
        bottomTrain.pauseTrain(); // Pausa na animacao do trem 2
      } // FIM if

      // Reseta o valor-velocidade dos trens na GUI
      velValue1.setText("0 Km/h"); // Valor da velocidade do trem 1
      velValue2.setText("0 Km/h"); // Valor da velocidade do trem 2

      if (topTrain.getPath().equals(firstPosition1) && bottomTrain.getPath().equals(firstPosition2)) {
      // Caso os trens estejam no primeiro caminho respectivamente (firsPosition1/2)
        resetBTN.fireEvent(e); // Reiniciaizamos a animacao para o momento zero e paramos a animacao

        // Mudamos a posicao dos trens para a posicao do segundo caminho (secPosition1/2) e definimos este novo caminho
        train1.translateXProperty().set(680); // Posicionamento X do trem 1
        train1.setTranslateY(162); // Posicionamento Y do trem 1
        train2.setTranslateX(680); // Posicionamento X do trem 2
        train2.setTranslateY(235); // Posicionamento Y do trem 2
        topTrain.setNewPath(secPosition1); // Novo caminho do trem 1
        bottomTrain.setNewPath(secPosition2); // Novo caminho do trem 2
        // E tambem definimos a velocidade dos trens para 0.10 ("10 Km/h")
        topTrain.setTrainSpeed(0.05); // Velocidade do trem 1
        bottomTrain.setTrainSpeed(0.05); // Velocidade do trem 2

      } else if (topTrain.getPath().equals(secPosition1) && bottomTrain.getPath().equals(secPosition2)) {
      // Caso os trens estejam no segundo caminho respectivamente (secPosition1/2):
        resetBTN.fireEvent(e); // Reiniciaizamos a animacao para o momento zero e paramos a animacao

        // Mudamos a posicao dos trens para a posicao do terceiro caminho (thirdPosition1/2) e definimos este novo caminho
        train1.setTranslateX(0); // Posicionamento X do trem 1
        train1.setTranslateY(162); // Posicionamento Y do trem 1
        train2.setTranslateX(680); // Posicionamento X do trem 2
        train2.setTranslateY(235); // Posicionamento Y do trem 2
        topTrain.setNewPath(thirdPosition1); // Novo caminho do trem 1
        bottomTrain.setNewPath(thirdPosition2); // Novo caminho do trem 2
        // E tambem definimos a velocidade dos trens para 0.10 ("10 Km/h")
        topTrain.setTrainSpeed(0.05); // Velocidade do trem 1
        bottomTrain.setTrainSpeed(0.05); // Velocidade do trem 2

      } else if (topTrain.getPath().equals(thirdPosition1) && bottomTrain.getPath().equals(thirdPosition2)) {
      // Caso os trens estejam no terceiro caminho respectivamente (thirdPosition1/2):
        resetBTN.fireEvent(e); // Reiniciaizamos a animacao para o momento zero e paramos a animacao

        // Mudamos a posicao dos trens para a posicao do quarto caminho (fourPosition1/2) e definimos este novo caminho
        train1.setTranslateX(680); // Posicionamento X do trem 1
        train1.setTranslateY(162); // Posicionamento Y do trem 1
        train2.setTranslateX(0); // Posicionamento X do trem 2
        train2.setTranslateY(235); // Posicionamento Y do trem 2
        topTrain.setNewPath(fourPosition1); // Novo caminho do trem 1
        bottomTrain.setNewPath(fourPosition2); // Novo caminho do trem 2
        // E tambem definimos a velocidade dos trens para 0.10 ("10 Km/h")
        topTrain.setTrainSpeed(0.05); // Velocidade do trem 1
        bottomTrain.setTrainSpeed(0.05); // Velocidade do trem 2
      } else {
      // Caso os trens estejam no quarto caminho respectivamente (fourPosition1/2):
        resetBTN.fireEvent(e); // Reiniciaizamos a animacao para o momento zero e paramos a animacao

        // Mudamos a posicao dos trens para a posicao do primeiro caminho (firsPosition1/2) e definimos este novo caminho
        train1.setTranslateX(0); // Posicionamento X do trem 1
        train1.setTranslateY(162); // Posicionamento Y do trem 1
        train2.setTranslateX(0); // Posicionamento X do trem 2
        train2.setTranslateY(235); // Posicionamento Y do trem 2
        topTrain.setNewPath(firstPosition1); // Novo caminho do trem 1
        bottomTrain.setNewPath(firstPosition2); // Novo caminho do trem 2
        // E tambem definimos a velocidade dos trens para 0.10 ("10 Km/h")
        topTrain.setTrainSpeed(0.05); // Velocidade do trem 1
        bottomTrain.setTrainSpeed(0.05); // Velocidade do trem 2
      } // FIM if-else
    }); // FIM Evento: click - trocar posicao dos trens

    /*
     * ***************************************************************
     * Evento: click
     * Funcao: define o metodo de exclusao mutua com variavel de travamento
     * Parametros: nao recebe parametros
     * Retorno: nao retorna valores
     */
    lockVarBTN.onMouseClickedProperty().set(e -> { // A cada click no botao de variavel de travamento
      topTrain.pauseTrain(); // Pausa na animacao do trem 1
      bottomTrain.pauseTrain(); // Pausa na animacao do trem 2
      topTrain.resetPos(false); // Reseta a posicao do trem 1
      bottomTrain.resetPos(false); // Reseta a posicao do trem 2
      playBTN.setText("Play"); // Texto do botao de play/pause
      g_mutualExclusion = 1; // Define o metodo de exclusao mutua com variavel de travamento

      methodsTitle.setText("Variável de Travamento"); // Atualiza o titulo dos metodos de acesso a regiao critica
    }); // FIM Evento: click - variavel de travamento

    /*
     * ***************************************************************
     * Evento: click
     * Funcao: define o metodo de exclusao mutua com alternancia estrita
     * Parametros: nao recebe parametros
     * Retorno: nao retorna valores
     */
    strictAltBTN.onMouseClickedProperty().set(e -> { // A cada click no botao de alternancia estrita
      topTrain.pauseTrain(); // Pausa na animacao do trem 1
      bottomTrain.pauseTrain(); // Pausa na animacao do trem 2
      topTrain.resetPos(false); // Reseta a posicao do trem 1
      bottomTrain.resetPos(false); // Reseta a posicao do trem 2    
      playBTN.setText("Play"); // Texto do botao de play/pause
      g_mutualExclusion = 2; // Define o metodo de exclusao mutua com alternancia estrita

      methodsTitle.setText("Alternância Estrita"); // Atualiza o titulo dos metodos de acesso a regiao critica
    }); // FIM Evento: click - alternancia estrita

    /*
     * ***************************************************************
     * Evento: click
     * Funcao: define o metodo de exclusao mutua de Peterson
     * Parametros: nao recebe parametros
     * Retorno: nao retorna valores
     */
    petersonBTN.onMouseClickedProperty().set(e -> { // A cada click no botao de peterson
      topTrain.pauseTrain(); // Pausa na animacao do trem 1
      bottomTrain.pauseTrain(); // Pausa na animacao do trem 2
      topTrain.resetPos(false); // Reseta a posicao do trem 1
      bottomTrain.resetPos(false); // Reseta a posicao do trem 2
      playBTN.setText("Play"); // Texto do botao de play/pause
      g_mutualExclusion = 3; // Define o metodo de exclusao mutua de Peterson

      methodsTitle.setText("Método de Peterson"); // Atualiza o titulo dos metodos de acesso a regiao critica
    }); // FIM Evento: click - peterson

    /*
     * ***************************************************************
     * Evento: click
     * Funcao: remove a exclusao mutua (permite colisoes)
     * Parametros: nao recebe parametros
     * Retorno: nao retorna valores
     */
    noneExcBTN.onMouseClickedProperty().set(e -> { // A cada click no botao de remover a exclusao mutua
      topTrain.pauseTrain(); // Pausa na animacao do trem 1
      bottomTrain.pauseTrain(); // Pausa na animacao do trem 2
      topTrain.resetPos(false); // Reseta a posicao do trem 1
      bottomTrain.resetPos(false); // Reseta a posicao do trem 2
      playBTN.setText("Play"); // Texto do botao de play/pause
      g_mutualExclusion = 0; // Remove a exclusao mutua

      methodsTitle.setText("Selecionar Exclusão Mútua"); // Atualiza o titulo dos metodos de acesso a regiao critica
    }); // FIM Evento: click - remover exclusao mutua
    /* FIM EVENTOS DE CLICK */

    primaryStage.show(); // Exibe a janela principal e inicia a aplicacao
  }

  // Outros metodos da classe Principal
  /**
   * *************************************************************
   * Metodo: createPath
   * Funcao: cria um caminho para o trem percorrer
   * Parametros: array de angulos, valor do eixo x, valor do eixo y, comprimento
   * do caminho
   * Retorno: nao retorna valores
   ***************************************************************
   * @param angles sequencia de angulos sobre os quais a linha do caminho vai girar para formar ou nao uma curva
   * @param x coordenada x do ponto inicial do caminho
   * @param y coordenada y do ponto inicial do caminho
   * @param length tamanho total do caminho
   * @return o caminho completo para o trem percorrer
   */
  private Path createPath(double[] angles, double x, double y, double length) {
    Path path = new Path(); // Instancia um novo objeto de caminho (Path)
    path.setStroke(Color.rgb(255, 255, 0, 1)); // Cor da linha do caminho
    path.getElements().add(new MoveTo(x, y)); // Define o ponto onde o caminho comeca

    for (double angle : angles) { // Para cada angulo no array de angulos
      if (angle != 0) { // Se o angulo for diferente de 0
        x += length / 3 * Math.cos(Math.toRadians(angle)); // Somamos aA coordenada x um terco do caminho total, multiplicado pelo cosseno do angulo
        y -= length / 3 * Math.sin(Math.toRadians(angle)); // Subtraimos da coordenada y um terco do caminho total, multiplicado pelo seno do angulo
        // Dessa forma, ao adicionar a nova LineTo (linha 633), uma nova linha ira do ponto anterior (por exemplo x=o, y=0) ate o ponto (x,y) calculado, fazendo uma "curva" no caminho
      } else { // Se o angulo for igual a 0
        x += length * Math.cos(Math.toRadians(angle)); // Somamos aA coordenada x o caminho total, multiplicado pelo cosseno de zero (1)
        y -= length * Math.sin(Math.toRadians(angle)); // Subtraimos da coordenada y o caminho total, multiplicado pelo seno de zero (0)
        // Dessa forma, ao adicionar a nova LineTo (linha 633), uma nova linha ira do ponto anterior (por exemplo x=o, y=0) ate o ponto (x,y) calculado, fazendo uma "reta" no caminho
      } // FIM if-else
      path.getElements().add(new LineTo(x, y)); // Adiciona ao caminho uma nova linha que parte da coordenada (x,y) anterior para nova que foi calculada no laco for
    } // FIM for

    return path; // Retorna o caminho completo para o trem percorrer
  } // FIM metodo createPath

  /**
   * ***************************************************************
   * Metodo: alertWindow
   * Funcao: cria uma janela de alerta
   * Parametros: mensagem de alerta
   * Retorno: nao retorna valores
   ***************************************************************
   * @param msg mensagem de alerta
   * @return nao retorna valores
   */
  public void alertWindow(String msg) {
    Stage alertStage = new Stage(); // Instancia um novo objeto Stage
    Pane alert = new Pane(); // Instancia um novo objeto painel para a janela de alerta
    Scene alertWindow = new Scene(alert, 300, 160); // Instancia uma nova cena para a janela de alerta

    alertStage.setTitle("Aviso!");
    alertStage.setScene(alertWindow);
    alertStage.setResizable(false);
    alertStage.centerOnScreen(); // Centraliza a janela de alerta na tela
    alertStage.initModality(Modality.APPLICATION_MODAL); // Bloqueia a janela principal enquanto a janela de alerta estiver aberta

    VBox alertVBox = new VBox(); // Instancia um novo objeto painel vertical para a janela de alerta
    alertVBox.setAlignment(Pos.CENTER); // Alinhamento dos elementos do painel vertical ao centro
    Label alertMsg = new Label(msg); // Definimos a mensagem de alerta conforme for passada por parametro
    alertMsg.setStyle("-fx-text-fill: #fff"); // Cor da mensagem de alerta
    Button alertBTN = new Button("OK"); // Instancia um novo objeto botao para fechar a janela de alerta
    alertBTN.cursorProperty().set(Cursor.HAND); // Cursor de mao ao passar o mouse por cima do botao de fechar a janela de alerta
    alertBTN.setPrefWidth(40); // Largura do botao de fechar a janela de alerta
    alertBTN.setPrefHeight(40); // Altura do botao de fechar a janela de alerta

    alertVBox.setStyle("-fx-background-image: url('img/tela-fundo.jpg');-fx-pref-width: 300px;-fx-pref-height: 160px;" +
        "\n-fx-padding: 5px;-fx-text-align: center;-fx-font-size: 14px;-fx-font-weight: bold;"); // Estilo do painel vertical da janela de alerta

    alertBTN.setStyle("-fx-background-image: url('img/smallBtn.png'); -fx-background-position: center center;" +
        "\n-fx-text-fill: #fff; -fx-font-weight: bold; -fx-font-size: 12px;");
    alertBTN.setTranslateY(20); // Posicionamento Y do botao de fechar a janela de alerta

    alertVBox.getChildren().addAll(alertMsg, alertBTN); // Adiciona os elementos ao painel vertical da janela de alerta
    alert.getChildren().addAll(alertVBox); // Adiciona o painel vertical aA janela de alerta

    alertBTN.onMouseClickedProperty().set(e -> { // A cada click no botao de fechar a janela de alerta
      alertStage.close(); // Fecha a janela de alerta
    }); // FIM Evento: click - fechar janela de alerta

    alertStage.getOnCloseRequest(); // Fecha a janela de alerta somente quando o usuario clicar no botao de fechar a janela de alerta
    alertStage.show(); // Exibe a janela de alerta
  } // FIM metodo alertWindow

  /**
   * ***************************************************************
   * Metodo: main
   * Funcao: executa todo o programa (neste caso, a aplicacao JavaFX)
   * Parametros: args
   * Retorno: nao retorna valores
   ***************************************************************
   * @param args
   * @return nao retorna valores
   */
  public static void main(String[] args) {
    launch(args); // Inicia a aplicacao JavaFX
  } // FIM metodo main
} // FIM classe Principal