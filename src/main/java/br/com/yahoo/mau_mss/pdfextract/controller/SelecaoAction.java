package br.com.yahoo.mau_mss.pdfextract.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import br.com.yahoo.mau_mss.pdfextract.view.DialogSelecao;

/**
 * <p>Title: SelecaoAction.java
 * <p>Description: Controller da tela de seleção de arquivos </p>
 * <p>Data: 4 de Abril de 2007, 19:41 </p>
 * @author Mauricio Soares da Silva
 * @version 1.0
 */

public class SelecaoAction implements ActionListener {
  private DialogSelecao dialogSelecao;
  private File file;
  public static final String COMANDO_SELECIONAR = "ApproveSelection";
  public static final String COMANDO_SAIR = "sair";
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  
  /** 
   * Creates a new instance of SelecaoAction
   * @param dialogSelecao DialogSelecao
   */
  public SelecaoAction(DialogSelecao dialogSelecao) {
    this.dialogSelecao = dialogSelecao;
    this.dialogSelecao.addActionListener(this);
  }
  
  /**
   * Controla a chamada para cada comando recebido
   * @param e ActionEvent
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    logger.info("Entrou no actionPerformed com comando: " + e.getActionCommand());
    if (e.getActionCommand().equals(SelecaoAction.COMANDO_SELECIONAR)) {
      this.seleciona();
    } else {
      this.sai();
    }
  }
  
  public File getFile(){
    return this.file;
  }
  
  private void seleciona() {
    this.file = this.dialogSelecao.getJFileChooser1();
    this.dialogSelecao.doClose(DialogSelecao.RET_CANCEL);
  }
  
  private void sai() {
    this.dialogSelecao.doClose(DialogSelecao.RET_CANCEL);
  }
  
}
