// Importacao de bibliotecas
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Path;
import javafx.util.Duration;
// Fim importacao de bibliotecas

public class Train extends Thread { // Classe que representa os trens (threads)
  // Atributos:
  private Pane trainPane; // Pane que representa o trem
  private Path trainPath; // Caminho que o trem vai percorrer
  private PathTransition trainMovement; // Movimentacao do trem
  private long trainId = this.threadId(); // Id do trem

  // Construtor:
  public Train(Pane trainPane, Path trainPath) {
    this.trainPane = trainPane;
    this.trainPath = trainPath;
    this.trainMovement = createPathTransition(trainPane, trainPath); // Cria a movimentacao do trem
    this.trainMovement.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT); // Orientacao do trem (sempre de frente)
  }

  /**
   * ***************************************************************
   * Metodo: run
   * Funcao: executa o codigo da thread
   * Parametros: nao recebe parametros
   * Retorno: nao retorna valor
   */
  @Override
  public void run() {
    while (true) { // Verifica sempre qual metodo de exclusao mutua foi escolhido
      switch (Principal.g_mutualExclusion) { // Switch-case para cada metodo de Exclusao Mutua
        case 1: // Variavel de Travamento
          while (true) { // Verifica a posicao do trem
            double xPos = this.trainPane.localToScene(this.trainPane.getBoundsInLocal()).getMinX(); // Pega a posicao do
                                                                                                    // trem

            if (xPos >= 270 && xPos <= 420) { // Se o trem estiver na RC1
              while (Principal.lock1 != 0) { // Enquanto a RC1 estiver trancada
                System.out.println(this.threadId() + ": Esperando antes da RC1");
                pauseTrain(); // Pausa o trem
              } // Fim while (Principal.lock1 != 0)

              playTrain(); // Se a RC1 estiver liberada, o trem continua

              Principal.lock1 = 1; // Trava a RC1, pois o trem atual entrou
              criticalregion1LV(); // Entra na RC1
              System.out.println(this.threadId() + ": Entrou na RC1");

              Principal.lock1 = 0; // Libera a RC1, pois o trem atual saiu
              System.out.println(this.threadId() + ": Saiu da RC1");

            } else { // Se o trem nao estiver na RC1
              try {
                Thread.sleep(100); // Sleep para evitar sobrecarga da CPU e liberar tempo para as outras threads
              } catch (InterruptedException e) {
                e.printStackTrace();
              } // Fim try-catch
            } // Fim if (xPos >= 270 && xPos <= 420)

            if (xPos >= 560 && xPos <= 700) { // Se o trem estiver na RC2
              while (Principal.lock2 != 0) { // Enquanto a RC2 estiver trancada
                System.out.println(this.threadId() + ": Esperando antes da RC2");
                pauseTrain(); // Pausa o trem
              } // Fim while (Principal.lock2 != 0)

              playTrain(); // Se a RC2 estiver liberada, o trem continua

              Principal.lock2 = 1; // Trava a RC2, pois o trem atual entrou
              criticalregion2LV(); // Entra na RC2
              System.out.println(this.threadId() + ": Entrou na RC2");

              Principal.lock2 = 0; // Libera a RC2, pois o trem atual saiu
              System.out.println(this.threadId() + ": Saiu da RC2");

            } else { // Se o trem nao estiver na RC2
              try {
                Thread.sleep(100); // Sleep para evitar sobrecarga da CPU e liberar tempo para as outras threads
              } catch (InterruptedException e) {
                e.printStackTrace();
              } // Fim try-catch
            } // Fim if (xPos >= 560 && xPos <= 700)

            if (Principal.g_mutualExclusion != 1) { // Se o usuario mudar o metodo de exclusao mutua, o loop eh quebrado
              break;
            } // Fim if (Principal.g_mutualExclusion != 1)
          } // Fim while (true) case 1
        case 2: // Alternancia Estrita
          while (true) { // Verifica a posicao do trem
            double xPos = this.trainPane.localToScene(this.trainPane.getBoundsInLocal()).getMinX(); // Pega a posicao do trem

            if (xPos >= 270 && xPos <= 420) { // Se o trem estiver na RC1
              if (this.trainId == 37) { // Se o trem for o 37
                while (Principal.turn1 != 0) { // Enquanto nao for a vez do trem 37
                  System.out.println(this.threadId() + ": Esperando antes da RC1");
                  pauseTrain(); // Pausa o trem
                } // Fim while (Principal.turn1 != 0)

                playTrain(); // Se for a vez do trem 37, o trem continua

                System.out.println(this.threadId() + ": Entrou na RC1");
                criticalregion1SA(this.trainId); // Entra na RC1

                Principal.turn1 = 1; // Passa a vez para o trem 38
                System.out.println(this.threadId() + ": Saiu da RC1");
              }

              if (this.trainId == 38) { // Se o trem for o 38
                while (Principal.turn1 != 1) { // Enquanto nao for a vez do trem 38
                  System.out.println(this.threadId() + ": Esperando antes da RC1");
                  pauseTrain(); // Pausa o trem
                } // Fim while (Principal.turn1 != 1)

                playTrain(); // Se for a vez do trem 38, o trem continua

                System.out.println(this.threadId() + ": Entrou na RC1");
                criticalregion1SA(this.trainId); // Entra na RC1

                Principal.turn1 = 0; // Passa a vez para o trem 37
                System.out.println(this.threadId() + ": Saiu da RC1");
              }

            } else {
              try {
                Thread.sleep(100); // Sleep para evitar sobrecarga da CPU e liberar tempo para as outras threads
              } catch (InterruptedException e) {
                e.printStackTrace();
              } // Fim try-catch
            } // Fim if (xPos >= 270 && xPos <= 420)

            if (xPos >= 560 && xPos <= 700) { // Se o trem estiver na RC2
              if (this.trainId == 37) { // Se o trem for o 37
                while (Principal.turn2 != 0) { // Enquanto nao for a vez do trem 37
                  System.out.println(this.threadId() + ": Esperando antes da RC2");
                  pauseTrain(); // Pausa o trem
                } // Fim while (Principal.turn2 != 0)

                playTrain(); // Se for a vez do trem 37, o trem continua

                System.out.println(this.threadId() + ": Entrou na RC2");
                criticalregion2SA(this.trainId); // Entra na RC2

                Principal.turn2 = 1; // Passa a vez para o trem 38
                System.out.println(this.threadId() + ": Saiu da RC2");
              } // Fim if (this.trainId == 37)

              if (this.trainId == 38) { // Se o trem for o 38
                while (Principal.turn2 != 1) { // Enquanto nao for a vez do trem 38
                  System.out.println(this.threadId() + ": Esperando antes da RC2");
                  pauseTrain(); // Pausa o trem
                }

                playTrain(); // Se for a vez do trem 38, o trem continua

                System.out.println(this.threadId() + ": Entrou na RC2");
                criticalregion2SA(this.trainId); // Entra na RC2

                Principal.turn2 = 0; // Passa a vez para o trem 37
                System.out.println(this.threadId() + ": Saiu da RC2");
              }

            } else { // Se o trem nao estiver na RC2
              try {
                Thread.sleep(100); // Sleep para evitar sobrecarga da CPU e liberar tempo para as outras threads
              } catch (InterruptedException e) {
                e.printStackTrace();
              } // Fim try-catch
            } // Fim if (xPos >= 560 && xPos <= 700)

            if (Principal.g_mutualExclusion != 2) { // Se o usuario mudar o metodo de exclusao mutua, o loop eh quebrado
              break;
            } // Fim if (Principal.g_mutualExclusion != 2)
          } // Fim while (true) case 2
        case 3: // Metodo de Peterson
          while (true) { // Verifica a posicao do trem
            double xPos = this.trainPane.localToScene(this.trainPane.getBoundsInLocal()).getMinX(); // Pega a posicao do
                                                                                                    // trem

            if (xPos >= 270 && xPos <= 420) { // Se o trem estiver na RC1
              if (this.trainId == 37) { // Se o trem for o 37
                enterCriticalRegion1PT(0); // Entra na RC1

              } else if (this.trainId == 38) { // Se o trem for o 38
                enterCriticalRegion1PT(1); // Entra na RC1
              } // Fim if-else
            } // Fim if (xPos >= 270 && xPos <= 420)

            if (xPos >= 560 && xPos <= 700) { // Se o trem estiver na RC2
              if (this.trainId == 37) { // Se o trem for o 37
                enterCriticalRegion2PT(0); // Entra na RC2

              } else if (this.trainId == 38) { // Se o trem for o 38
                enterCriticalRegion2PT(1); // Entra na RC2
              } // Fim if-else
            } // Fim if (xPos >= 560 && xPos <= 700)

            if (Principal.g_mutualExclusion != 3) { // Se o usuario mudar o metodo de exclusao mutua, o loop eh quebrado
              break;
            }
          }
        default: // Sem exclusao mutua (colisoes permitidas)
          while (true) { // Loop que itera ate que o metodo de exclusao mutua seja alterado
            Platform.runLater(() -> { // Metodo que garante que o trem seja executado corretamente na aplicacao JavaFX
              playTrain(); // Inicia a animacao do trem
            });
            try {
              Thread.sleep(100); // Sleep para evitar sobrecarga da CPU e liberar tempo para as outras threads
            } catch (InterruptedException e) {
              e.printStackTrace();
            } // Fim try-catch
            
            if (Principal.g_mutualExclusion != 0) { // Se o usuario mudar o metodo de exclusao mutua, o loop eh quebrado
              break;
            } // Fim if (Principal.g_mutualExclusion != 0
          } // Fim while (true) case default
      } // Fim switch-case

      try {
        Thread.sleep(100); // Sleep para evitar sobrecarga da CPU e liberar tempo para as outras threads
      } catch (InterruptedException e) {
        e.printStackTrace();
      } // Fim try-catch
    }
  }

  // Getters e Setters:
  public Pane getTrain() {
    return trainPane;
  }

  public void setNewTrain(Pane newTrainPane) {
    this.trainPane = newTrainPane;
  }

  public Path getPath() {
    return trainPath;
  }

  public void setNewPath(Path newTrainPath) {
    this.trainPath = newTrainPath;
    this.trainMovement.setPath(newTrainPath);
  }

  public double getTrainSpeed() {
    return trainMovement.getRate();
  }

  public void setTrainSpeed(double speed) {
    this.trainMovement.setRate(speed);
  };

  public PathTransition getTrainMovement() {
    return trainMovement;
  }

  public void setTrainMovement(PathTransition newTrainMovement) {
    this.trainMovement = newTrainMovement;
  }

  public long getTrainId() {
    return trainId;
  }

  public void setTrainId(long trainId) {
    this.trainId = trainId;
  }

  // Outros metodos
  /**
   * ***************************************************************
   * Metodo: playTrain
   * Funcao: inicia a movimentacao dos trens (ou reinicia apos um pause)
   * Parametros: nao recebe parametros
   * Retorno: nao retorna valor
   */
  public void playTrain() {
    trainMovement.play(); // Inicia a animacao do trem
  } // Fim playTrain

  /**
   * ***************************************************************
   * Metodo: pauseTrain
   * Funcao: pausa a movimentacao dos trens
   * Parametros: nao recebe parametros
   * Retorno: nao retorna valor
   */
  public void pauseTrain() {
    trainMovement.pause(); // Pausa a animacao do trem
  } // Fim pauseTrain

  /**
   * ***************************************************************
   * Metodo: resetPos
   * Funcao: move os trens da posicao atual para a posicao inicial da animacao
   * Parametros: rebece um boolean
   * Retorno: nao retorna valor
   *************************************************************** 
   * @param isMoving flag que indica se o trem esta em movimento ou nao
   */
  public void resetPos(boolean isMoving) {
    if (isMoving) { // Se o trem estiver em movimento
      trainMovement.jumpTo(Duration.ZERO); // Volta o trem para o inicio da animacao
      trainMovement.playFromStart(); // Inicia a animacao do trem
    } else { // Se o trem nao estiver em movimento
      trainMovement.jumpTo(Duration.ZERO); // Volta o trem para o inicio da animacao
      trainMovement.stop(); // Mantem a animacao do trem parada
    } // Fim if-else
  } // Fim resetPos

  /**
   * ***************************************************************
   * Metodo: createPathTransition
   * Funcao: define a movimentacao do trem
   * Parametros: o trem (aqui criado a patir de um Pane) e o caminho do trem
   * (criado na funcao anterior - createPath)
   * Retorno: nao retorna valores
   ***************************************************************
   * @param trainPane painel que representa o trem
   * @param trainPath caminho que o trem vai percorrer
   * @return transicao/animacao do trem completa
   */
  public PathTransition createPathTransition(Pane trainPane, Path trainPath) {
    PathTransition pathTransition = new PathTransition(); // Cria o objeto animacao do trem
    pathTransition.setPath(trainPath); // Define caminho da animacao
    pathTransition.setNode(trainPane); // Define o elemento que vai se mover pelo caminho
    pathTransition.setInterpolator(Interpolator.LINEAR); // Matem a animacao linear (sem que os trens acelerem ou
                                                         // desacelerem no inicio ou fim)
    pathTransition.setRate(0.05); // Velocidade inicial dos trens (0.05 = 5 Km/h)
    pathTransition.setCycleCount(PathTransition.INDEFINITE); // Repeticao indefinida (ate que o usuario clique em reset)
    pathTransition.setAutoReverse(false); // Nao inverte a animacao (o trem volta pro inicio ao terminar o percurso)

    return pathTransition; // Retorna a animacao completa
  } // Fim createPathTransition

  /**
   * ***************************************************************
   * Metodo: enterCriticalRegion1PT
   * Funcao: define que o trem entrou na regiao critica 1 com metodo de Peterson
   * Parametros: recebe o id do trem/thread
   * Retorno: nao retorna valor
   ***************************************************************
   * @param i id do trem/thread
   */
  private void enterCriticalRegion1PT(int i) {
    int j = 1 - i; // Int que representa um outro trem
    Principal.flagRC1[i] = true; // Flag que indica que o trem atual quer entrar na RC1
    Principal.turnRC1 = j; // Define a vez do outro trem

    while (Principal.flagRC1[j] && Principal.turnRC1 == j) { // Enquanto o outro trem quiser entrar na RC1 e for a vez
                                                             // dele
      System.out.println("Trem " + this.trainId + " esperando antes da RC1");
      Platform.runLater(() -> {
        pauseTrain(); // Pausa o trem atual
      });
    } // Fim while (Principal.flagRC1[j] && Principal.turnRC1 == j)

    System.out.println("Trem " + this.trainId + " entrou na região crítica 1");
    Platform.runLater(() -> {
      playTrain(); // Se o outro trem nao quiser entrar na RC1, o trem atual continua
    });

    while (true) { // Loop que verifica se o trem saiu da RC1
      double xPos = this.trainPane.localToScene(this.trainPane.getBoundsInLocal()).getMinX(); // Pega a posicao do trem

      if (xPos >= 420) { // Se o trem sair da RC1
        outOfCriticalRegionPT1(i); // Chamamos o metodo que indica que o trem saiu da RC1
        System.out.println("Trem " + this.trainId + " saiu da região crítica 1");

        break; // Quebramos o loop
      } // Fim if (xPos >= 420)

      try {
        Thread.sleep(100); // Sleep para evitar sobrecarga da CPU e liberar tempo para as outras threads
      } catch (InterruptedException e) {
        e.printStackTrace();
      } // Fim try-catch
    } // Fim while (true)
  } // Fim enterCriticalRegion1PT

  /**
   * ***************************************************************
   * Metodo: enterCriticalRegion2PT
   * Funcao: define que o trem entrou na regiao critica 2 com metodo de Peterson
   * Parametros: recebe o id do trem/thread
   * Retorno: nao retorna valor
   ***************************************************************
   * @param i id do trem/thread
   */
  private void enterCriticalRegion2PT(int i) {
    int j = 1 - i; // Int que representa um outro trem
    Principal.flagRC2[i] = true; // Flag que indica que o trem atual quer entrar na RC2
    Principal.turnRC2 = j; // Define a vez do outro trem

    while (Principal.flagRC2[j] && Principal.turnRC2 == j) { // Enquanto o outro trem quiser entrar na RC2 e for a vez
                                                             // dele
      System.out.println("Trem " + this.trainId + " esperando antes da RC2");
      Platform.runLater(() -> {
        pauseTrain(); // Pausa o trem atual
      });
    } // Fim while (Principal.flagRC2[j] && Principal.turnRC2 == j)

    System.out.println("Trem " + this.trainId + " entrou na região crítica 2");
    Platform.runLater(() -> {
      playTrain(); // Se o outro trem nao quiser entrar na RC2, o trem atual continua
    });

    while (true) { // Loop que verifica se o trem saiu da RC2
      double xPos = this.trainPane.localToScene(this.trainPane.getBoundsInLocal()).getMinX(); // Pega a posicao do trem

      if (xPos >= 700) { // Se o trem sair da RC2
        outOfCriticalRegionPT2(i); // Chamamos o metodo que indica que o trem saiu da RC2
        System.out.println("Trem " + this.trainId + " saiu da região crítica 2");

        break; // Quebramos o loop
      } // Fim if (xPos >= 700)

      try {
        Thread.sleep(100); // Sleep para evitar sobrecarga da CPU e liberar tempo para as outras threads
      } catch (InterruptedException e) {
        e.printStackTrace();
      } // Fim try-catch
    } // Fim while (true)
  } // Fim enterCriticalRegion2PT

  /**
   * ***************************************************************
   * Metodo: outOfCriticalRegionPT1
   * Funcao: define que o trem saiu da regiao critica 1 com o metodo de Peterson
   * Parametros: recebe o id do trem/thread
   * Retorno: nao retorna valor
   ***************************************************************
   * @param i id do trem/thread
   */
  private void outOfCriticalRegionPT1(int i) {
    Principal.flagRC1[i] = false; // Flag que indica que o trem atual nao tem mais interesse em entrar na RC1
                                  // (provavelmente porque ja saiu dela)
  } // Fim outOfCriticalRegionPT1

  /**
   * ***************************************************************
   * Metodo: outOfCriticalRegionPT2
   * Funcao: define que o trem saiu da regiao critica 2 com o metodo de Peterson
   * Parametros: recebe o id do trem/thread
   * Retorno: nao retorna valor
   ***************************************************************
   * @param i id do trem/thread
   */
  private void outOfCriticalRegionPT2(int i) {
    Principal.flagRC2[i] = false; // Flag que indica que o trem atual nao tem mais interesse em entrar na RC2
                                  // (provavelmente porque ja saiu dela)
  } // Fim outOfCriticalRegionPT2

  /**
   * ***************************************************************
   * Metodo: criticalregion1SA
   * Funcao: trava/libera regiao critica 1 com metodo de alternancia estrita
   * Parametros: recebe o id do trem/thread
   * Retorno: nao retorna valor
   ***************************************************************
   * @param trainId id do trem/thread
   */
  private void criticalregion1SA(long trainId) {
    while (true) { // Loop que verifica a posicao do trem
      System.out.println("Trem " + this.trainId + " está na região crítica");

      double xPos = this.trainPane.localToScene(this.trainPane.getBoundsInLocal()).getMinX(); // Pega a posicao do trem
      if (xPos >= 430 || xPos <= 260) { // Se o trem sair da RC1
        System.out.println("Trem " + this.trainId + " saiu da região crítica");
        break; // Quebramos o loop
      } // Fim if (xPos >= 430 || xPos <= 260)

      try {
        Thread.sleep(1000); // Sleep para evitar sobrecarga da CPU e liberar tempo para as outras threads
      } catch (InterruptedException e) {
        e.printStackTrace();
      } // Fim try-catch
    } // Fim while (true)
  } // Fim criticalregion1SA

  /**
   * ***************************************************************
   * Metodo: criticalregion2SA
   * Funcao: trava/libera regiao critica 2 com metodo de alternancia estrita
   * Parametros: recebe o id do trem/thread
   * Retorno: nao retorna valor
   ***************************************************************
   * @param trainId id do trem/thread
   */
  private void criticalregion2SA(long trainId) {
    while (true) { // Loop que verifica a posicao do trem
      System.out.println("Trem " + this.trainId + " está na região crítica");

      double xPos = this.trainPane.localToScene(this.trainPane.getBoundsInLocal()).getMinX(); // Pega a posicao do trem
      if (xPos >= 710 || xPos <= 550) { // Se o trem sair da RC2
        System.out.println("Trem " + this.trainId + " saiu da região crítica");
        break; // Quebramos o loop
      } // Fim if (xPos >= 710 || xPos <= 550)

      try {
        Thread.sleep(1000); // Sleep para evitar sobrecarga da CPU e liberar tempo para as outras threads
      } catch (InterruptedException e) {
        e.printStackTrace();
      } // Fim try-catch
    } // Fim while (true)
  } // Fim criticalregion2SA

  /**
   * ***************************************************************
   * Metodo: criticalregion1LV
   * Funcao: trava/libera regiao critica 1 com metodo de lock variable
   * Parametros: nao recebe parametros
   * Retorno: nao retorna valor
   */
  private void criticalregion1LV() {
    while (true) { // Loop que verifica a posicao do trem
      double xPos = this.trainPane.localToScene(this.trainPane.getBoundsInLocal()).getMinX(); // Pega a posicao do trem
      if (xPos >= 420 || xPos <= 270) { // Se o trem estiver fora da RC1
        break; // Quebramos o loop
      } // Fim if (xPos >= 420 || xPos <= 270)

      try {
        Thread.sleep(100); // Sleep para evitar sobrecarga da CPU e liberar tempo para as outras threads
      } catch (InterruptedException e) {
        e.printStackTrace();
      } // Fim try-catch
    } // Fim while (true)
  } // Fim criticalregion1LV

  /**
   * ***************************************************************
   * Metodo: criticalregion2LV
   * Funcao: trava/libera regiao critica 2 com metodo de lock variable
   * Parametros: nao recebe parametros
   * Retorno: nao retorna valor
   */
  private void criticalregion2LV() {
    while (true) { // Loop que verifica a posicao do trem
      double xPos = this.trainPane.localToScene(this.trainPane.getBoundsInLocal()).getMinX(); // Pega a posicao do trem
      if (xPos >= 700 || xPos <= 560) { // Se o trem estiver fora da RC2
        break; // Quebramos o loop
      } // Fim if (xPos >= 700 || xPos <= 560)

      try {
        Thread.sleep(100); // Sleep para evitar sobrecarga da CPU e liberar tempo para as outras threads
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

} // Fim da classe Train