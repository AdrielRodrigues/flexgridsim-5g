# Flexgridsim 5G

Simulador de eventos discretos para Redes Ópticas Elásticas com Multiplexação por Divisão Espacial (SDM-EON) adaptado para a arquitetura Cloud Fog Radio Access Network (CF-RAN) de comunicação 5G.

# Título

Latency-Aware Routing and Multidimensional Optical Resource Allocation for CF-RAN over SDM-EON
Autores: Edson Adriel Rodrigues, Daniel Macêdo Batista, Helder Oliveira

# Selos Considerados

Artefatos Disponíveis (SeloD)
Artefatos Funcionais (SeloF)
Artefatos Sustentáveis (SeloS)
Experimentos Reprodutíveis (SeloR)

# Informações básicas, Dependências e Instalação

Recomenda-se usar Java IDE (preferência por Eclipse)

É necessário ter a versão do Java 21, sem dependências adicionais

Para a fase de plot dos gráficos, é recomendado que se use Python, com dependências adicionais descritas em /Plotting/requirements.txt

# Experimentos

Para executar a simulação, deve-se selecionar um arquivo XML, que contém a informação da topologia e do algoritmo a ser executado.

Usage: FlexGridSim.jar xml_file number_of_simulations [-trace] [-verbose] [minload maxload step]

- trace: opcional
- verbose: opcional

Parâmetros utilizados:
- number of simulations: 10
- minload: 100
- maxload: 500
- step: 50

O resultado é gerado em um arquivo .dat com o nome contido no XML para cada uma das métricas. Os arquivos são encontrados na pasta flexgridsim/output.

Os arquivos .dat são usados para o plotting que é escrito em Python e gera os gráficos para as métricas.

# LICENSE

Consulte o arquivo LICENSE para mais detalhes.
