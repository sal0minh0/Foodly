package com.foodly.Service;

import com.foodly.DAO.Cliente.ClienteDAO;
import com.foodly.DAO.UsuarioDAO;
import com.foodly.Models.Cliente.Cliente;
import com.foodly.Models.Usuario;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClienteService {

    private final UsuarioDAO usuarioDAO;
    private final ClienteDAO clienteDAO;

    public ClienteService() {
        this.usuarioDAO = new UsuarioDAO();
        this.clienteDAO = new ClienteDAO();
    }

    public Cliente cadastrarCliente(String nome, String email, String senhaHash, 
                                   String telefone, String enderecoPadrao) throws SQLException {
        // Cria o usuário
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenhaHash(senhaHash); 
        usuario.setTelefone(telefone);
        usuario.setTipoUsuario("cliente");
        usuario.setCriadoEm(LocalDateTime.now());

        int usuarioId = usuarioDAO.salvar(usuario);

        // Cria o cliente associado ao usuário
        Cliente cliente = new Cliente();
        cliente.setUsuarioId(usuarioId);
        cliente.setEnderecoPadrao(enderecoPadrao);

        int clienteId = clienteDAO.salvar(cliente);
        cliente.setId(clienteId);

        return cliente;
    }

    public Cliente buscarClientePorId(int id) throws SQLException {
        return clienteDAO.buscarPorId(id);
    }

    public List<Cliente> listarClientes() throws SQLException {
        return clienteDAO.listarTodos();
    }

    public void atualizarCliente(Cliente cliente) throws SQLException {
        clienteDAO.atualizar(cliente);
    }

    public void deletarCliente(int id) throws SQLException {
        clienteDAO.deletar(id);
    }
}
