import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class App {
    private static List<Cliente> listaClientes = new ArrayList<>();
    private static List<Produto> listaProdutos = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static int contadorNota = 1;
    private static Random random = new Random();
    
    private static int ultimaNotaNumero = 0;
    private static String ultimaNotaChave = "Nenhuma nota emitida";

    public static void main(String[] args) throws Exception {
       int opcao = 0;

        do {
            System.out.println("\n=======================================");
            System.out.println("     SISTEMA DE EMISSÃO DE NOTAS       ");
            System.out.println("=======================================");
            System.out.println("1. [Aba] Clientes (Cadastrar)");
            System.out.println("2. [Aba] Estoque (Cadastrar Produto / Inserir Saldo)");
            System.out.println("3. [Aba] Emissão de Nota Fiscal");
            System.out.println("4. Sair");
            System.out.print("Escolha a aba que você deseja acessar: ");
            
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    abaClientes();
                    break;
                case 2:
                    abaEstoque();
                    break;
                case 3:
                    abaEmissaoNota();
                    break;
                case 4:
                    System.out.println("\nFechando o sistema...");
                    if (ultimaNotaNumero > 0) {
                        System.out.println("Última Nota Fiscal emitida nº: " + ultimaNotaNumero);
                        System.out.println("Chave de acesso: " + ultimaNotaChave);
                    } else {
                        System.out.println("Nenhuma nota fiscal foi emitida nesta sessão.");
                    }
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        } while (opcao != 4);
    }

    private static void abaClientes() {
        System.out.println("\n--- [ABA] CADASTRO DE CLIENTE ---");
        System.out.print("Nome do Cliente: ");
        String nome = scanner.nextLine();
        System.out.print("Código do Cliente: ");
        String codigo = scanner.nextLine();

        Cliente novoCliente = new Cliente(nome, codigo);
        listaClientes.add(novoCliente);
        System.out.println("Cliente '" + nome + "' cadastrado com sucesso!");
    }

    private static void abaEstoque() {
        System.out.println("\n--- [ABA] ESTOQUE E PRODUÇÃO ---");
        System.out.println("1. Cadastrar Novo Produto no Catálogo");
        System.out.println("2. Lançar Produção (Inserir Saldo)");
        System.out.print("Escolha uma opção: ");
        int subOpcao = scanner.nextInt();
        scanner.nextLine(); 

        if (subOpcao == 1) {
            System.out.print("Código do Produto: ");
            String cod = scanner.nextLine();
            System.out.print("Nome do Produto: ");
            String nome = scanner.nextLine();

            listaProdutos.add(new Produto(cod, nome));
            System.out.println("Produto '" + nome + "' adicionado ao catálogo!");

        } else if (subOpcao == 2) {
            if (listaProdutos.isEmpty()) {
                System.out.println("Nenhum produto cadastrado no sistema ainda.");
                return;
            }
            System.out.print("Digite o código do produto produzido: ");
            String cod = scanner.nextLine();

            Produto produto = buscarProduto(cod);

            if (produto != null) {
                System.out.print("Quantidade produzida (Saldo a ser inserido): ");
                int quantidade = scanner.nextInt();
                scanner.nextLine();

                produto.adicionarProducao(quantidade);
                System.out.println("Saldo atualizado! Novo saldo de " + produto.getNome() + ": " + produto.getSaldoEstoque());
            } else {
                System.out.println("Produto com o código '" + cod + "' não foi encontrado.");
            }
        } else {
            System.out.println("Opção inválida.");
        }
    }

    private static void abaEmissaoNota() {
        System.out.println("\n--- [ABA] EMISSÃO DE NOTA FISCAL ---");

        if (listaClientes.isEmpty()) {
            System.out.println("Cadastre um cliente na Aba 1 antes de emitir a nota.");
            return;
        }
        if (listaProdutos.isEmpty()) {
            System.out.println("Cadastre um produto e insira saldo na Aba 2 antes de emitir.");
            return;
        }

        System.out.print("Código do Cliente: ");
        String codCli = scanner.nextLine();
        Cliente cli = buscarCliente(codCli);

        if (cli == null) {
            System.out.println("Cliente não encontrado!");
            return;
        }

        System.out.print("Código do Produto: ");
        String codProd = scanner.nextLine();
        Produto prod = buscarProduto(codProd);

        if (prod == null) {
            System.out.println("Produto não encontrado!");
            return;
        }

        System.out.print("Quantidade a ser emitida na nota: ");
        int quantidadeNota = scanner.nextInt();
        scanner.nextLine();

        if (quantidadeNota > prod.getSaldoEstoque()) {
            System.out.println("Saldo insuficiente! Estoque atual: " + prod.getSaldoEstoque());
            return;
        }

        prod.deduzirEstoque(quantidadeNota);

        // Aqui geramos os dados e salvamos nas variáveis corretas
        ultimaNotaChave = geradorChave15Digitos();
        ultimaNotaNumero = contadorNota++;

        System.out.println("\n==================================================");
        System.out.println("                NOTA FISCAL ELETRÔNICA            ");
        System.out.println("==================================================");
        System.out.println(" NOTA FISCAL Nº: " + ultimaNotaNumero);
        System.out.println(" CHAVE DE ACESSO: " + ultimaNotaChave);
        System.out.println("--------------------------------------------------");
        System.out.println(" CLIENTE: " + cli.getNome());
        System.out.println(" CÓD. CLIENTE: " + cli.getCodigo());
        System.out.println("--------------------------------------------------");
        System.out.println(" PRODUTO: " + prod.getNome());
        System.out.println(" CÓD. PRODUTO: " + prod.getCodigo());
        System.out.println(" QTD EMITIDA: " + quantidadeNota);
        System.out.println("--------------------------------------------------");
        System.out.println(" EMISSÃO CONCLUÍDA COM SUCESSO! ");
        System.out.println(" Saldo restante no estoque: " + prod.getSaldoEstoque());
        System.out.println("==================================================\n");
    }
    
    private static String geradorChave15Digitos() {
        StringBuilder chave = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            chave.append(random.nextInt(10));
        }
        return chave.toString();
    }

    private static Produto buscarProduto(String codigo) {
        for (Produto p : listaProdutos) {
            if (p.getCodigo().equalsIgnoreCase(codigo)) {
                return p;
            }
        }
        return null;
    }

    private static Cliente buscarCliente(String codigo) {
        for (Cliente c : listaClientes) {
            if (c.getCodigo().equalsIgnoreCase(codigo)) {
                return c;
            }
        }
        return null;
    }
}

class Cliente {
    private String nome;
    private String codigo;

    public Cliente(String nome, String codigo) {
        this.nome = nome;
        this.codigo = codigo;
    }

    public String getNome() { return nome; }
    public String getCodigo() { return codigo; }
}

class Produto {
    private String codigo;
    private String nome;
    private int saldoEstoque; 

    public Produto(String codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
        this.saldoEstoque = 0;
    }

    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public int getSaldoEstoque() { return saldoEstoque; }

    public void adicionarProducao(int quantidade) {
        this.saldoEstoque += quantidade;
    }

    public void deduzirEstoque(int quantidade) {
        this.saldoEstoque -= quantidade;
    }
}
