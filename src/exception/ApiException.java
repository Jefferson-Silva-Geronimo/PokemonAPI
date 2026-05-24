package exception;

/**
 * Exceção personalizada para erros relacionados à comunicação com a API.
 *
 * Utilizada para encapsular falhas ao realizar requisições HTTP
 * ou ao processar respostas da PokéAPI.
 *
 * Objetivo:
 * - Padronizar erros de API
 * - Facilitar tratamento de exceções no sistema
 *
 * Conceitos aplicados:
 * - Exceções customizadas
 * - Tratamento de erros
 */
public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}