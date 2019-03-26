package forms.loanbroker;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import shared.bank.*;
import shared.loan.*;

public class LoanBrokerFrame extends JFrame {

	private static LoanClientAppGateway loanClientAppGateway;
	private static BankAppGateway bankAppGateway;
	private static Map<String, LoanRequest> loanRequests;

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private DefaultListModel<JListLine> listModel = new DefaultListModel<>();
	private JList<JListLine> list;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoanBrokerFrame frame = new LoanBrokerFrame();
					loanClientAppGateway = new LoanClientAppGateway(frame);
					bankAppGateway = new BankAppGateway(frame);
					loanRequests = new HashMap<>();

					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LoanBrokerFrame() {
		setTitle("Loan Broker");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{46, 31, 86, 30, 89, 0};
		gbl_contentPane.rowHeights = new int[]{233, 23, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 7;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		list = new JList<>(listModel);
		scrollPane.setViewportView(list);
	}
	
	private JListLine getLine(LoanRequest request){
		for (int i = 0; i < listModel.getSize(); i++){
			JListLine rr =listModel.get(i);
			if (rr.getLoanRequest() == request){
				return rr;
			}
		}
		return null;
	}
	
	public void add(LoanRequest loanRequest, String corrolationId){
		loanRequests.put(corrolationId, loanRequest);
		listModel.addElement(new JListLine(loanRequest));
		BankInterestRequest bankInterestRequest = new BankInterestRequest(loanRequest.getAmount(), loanRequest.getTime());
		bankAppGateway.sendBankRequest(bankInterestRequest, corrolationId);
		this.add(loanRequest, bankInterestRequest);
	}

	public void add(LoanRequest loanRequest, BankInterestRequest bankRequest){
		JListLine rr = getLine(loanRequest);
		if (rr!= null && bankRequest != null){
			rr.setBankRequest(bankRequest);
            list.repaint();
		}
	}
	
	public void add(String corrolationId, BankInterestReply bankReply){
		JListLine rr = getLine(loanRequests.get(corrolationId));
		if (rr!= null && bankReply != null){
			rr.setBankReply(bankReply);
            list.repaint();
		}
		loanClientAppGateway.sendLoanReply(new LoanReply(bankReply.getInterest(), bankReply.getQuoteId()), corrolationId);
	}
}
