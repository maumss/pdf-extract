package br.com.yahoo.mau_mss.pdfextract.controller;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import org.apache.commons.lang3.StringUtils;
import br.com.yahoo.mau_mss.pdfextract.service.CreateImages;
import br.com.yahoo.mau_mss.pdfextract.view.DialogSelecao;
import br.com.yahoo.mau_mss.pdfextract.view.PdfExtractApp;

/**
 * <p>Title: CalibreHelperAppAction</p>
 * <p>Description:  </p>
 * <p>Date: Jun 20, 2012, 8:15:55 AM</p>
 * @author Mauricio Soares da Silva (mauricio.soares)
 */
public class PdfExtractAppAction implements ActionListener {
  private PdfExtractApp calibreHelperApp;
  private DialogSelecao dialogSelecao;
  private Timer timer;
  private CreateImages geraHtm;
  private File pdfFile;
  public static final String COMANDO_PROCESSAR = "processar";
  public static final String COMANDO_ACHARINPUT = "acharInput";
  public static final String COMANDO_CANCELAR = "cancelar";
  public static final int ONE_SECOND = 1000;

  /**
   * Create a new instance of <code>CalibreHelperAppAction</code>.
   */
  public PdfExtractAppAction(PdfExtractApp calibreHelperApp) {
    this.calibreHelperApp = calibreHelperApp;
    this.calibreHelperApp.addActionListener(this);
   iniciarTimer();
  }

  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals(PdfExtractAppAction.COMANDO_PROCESSAR)) 
      processar();
    else if (e.getActionCommand().equals(PdfExtractAppAction.COMANDO_ACHARINPUT)) 
      acharInput();
    else if (e.getActionCommand().equals(PdfExtractAppAction.COMANDO_CANCELAR)) 
      sair();
    else
      throw new IllegalArgumentException("Comando " + e.getActionCommand() + " desconhecido");
  }
  
  private void iniciarTimer() {
    // cria um timer
    this.timer = new Timer(PdfExtractAppAction.ONE_SECOND, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            if (geraHtm == null)
              return;
            calibreHelperApp.ajustaJProgressBarEspecific(0, geraHtm.getTamanhoEspecifico());
            if (geraHtm.getAtualEspecifico() >= calibreHelperApp.getJProgressBarEspecificMax()) {
              calibreHelperApp.setJProgressBarEspecific(calibreHelperApp.getJProgressBarEspecificMax());
            } // end if
            else {
              calibreHelperApp.setJProgressBarEspecific(geraHtm.getAtualEspecifico());
            } // end else
            calibreHelperApp.setJLabelEspecific(geraHtm.getMensagemEspecifico());
            if (geraHtm.estaFeito()) {
              Toolkit.getDefaultToolkit().beep();
              timer.stop();
              calibreHelperApp.desligaCursorEspera();
              calibreHelperApp.setJProgressBarEspecific(calibreHelperApp.getJProgressBarEspecificMin());
              JOptionPane.showMessageDialog(calibreHelperApp, "Geração concluída.",
                      "Informação", JOptionPane.INFORMATION_MESSAGE);
              geraHtm = null;
            } // end if
        } // end aciontPerformed
    }); // timer
  }
  
  private boolean validar() {
    boolean result = true;
    if (StringUtils.isBlank(this.calibreHelperApp.getJTextFieldInput()) ||
        this.calibreHelperApp.getJTextFieldInput().length() < 4) {
      JOptionPane.showMessageDialog(calibreHelperApp, "Diretório de entrada inválido",
                      "Erro", JOptionPane.ERROR_MESSAGE);
      result = false;
    }
    return result;
  }
  
  private void processar() {
    if (! validar())
      return;
    this.calibreHelperApp.ligaCursorEspera();
    this.geraHtm = new CreateImages(this.pdfFile);
    geraHtm.go();
    timer.start();
  }
  
  private File getPdf(){
    this.dialogSelecao = new DialogSelecao(null, true);
    SelecaoAction selecaoAction = new SelecaoAction(this.dialogSelecao);
    this.dialogSelecao.setVisible(true);
    return selecaoAction.getFile(); //SystemUtils.FILE_SEPARATOR;
  }
  
  private void acharInput() {
    this.pdfFile = getPdf();
    this.calibreHelperApp.setJTextFieldInput(this.pdfFile.getAbsolutePath());
  }
  
  private void sair() {
    Toolkit.getDefaultToolkit().beep();
    this.timer.stop();
    if (this.geraHtm != null)
      this.geraHtm.stop();
    this.calibreHelperApp.desligaCursorEspera();
    System.exit(0);
  }

}
