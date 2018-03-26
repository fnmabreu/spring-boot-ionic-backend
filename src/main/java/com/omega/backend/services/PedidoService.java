package com.omega.backend.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.omega.backend.domain.ItemPedido;
import com.omega.backend.domain.PagamentoComBoleto;
import com.omega.backend.domain.Pedido;
import com.omega.backend.domain.enums.EstadoPagamento;
import com.omega.backend.repositories.ClienteRepository;
import com.omega.backend.repositories.ItemPedidoRepository;
import com.omega.backend.repositories.PagamentoRepository;
import com.omega.backend.repositories.PedidoRepository;
import com.omega.backend.repositories.ProdutoRepository;
import com.omega.backend.services.exception.ObjectNotFoundException;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository repo;

	@Autowired
	private BoletoService boletoService;

	@Autowired
	private PagamentoRepository pagamentoRepo;

	@Autowired
	private ProdutoRepository produtoRepo;

	@Autowired
	private ItemPedidoRepository itemPedidoRepo;

	@Autowired
	private ClienteRepository clienteRepo;

	@Autowired
	private EmailService emailService;

	public Pedido find(Integer id) {

		Optional<Pedido> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Pedido.class.getName()));

	}

	public Pedido insert(Pedido obj) {

		obj.setId(null);
		obj.setInstante(new Date());
		obj.setCliente(clienteRepo.findById(obj.getCliente().getId()).orElse(null));
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);

		if (obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());
		}

		// Save do pedido
		obj = repo.save(obj);

		// Save do Pagamento
		pagamentoRepo.save(obj.getPagamento());

		for (ItemPedido ip : obj.getItens()) {
			ip.setDesconto(0.0);
			ip.setProduto(produtoRepo.findById(ip.getProduto().getId()).orElse(null));
			ip.setPreco(ip.getProduto().getPreco());
			ip.setPedido(obj);
		}

		// Save dos Itens
		itemPedidoRepo.saveAll(obj.getItens());

		emailService.sendOrderConfirmationHtmlEmail(obj);

		return obj;
	}

}
