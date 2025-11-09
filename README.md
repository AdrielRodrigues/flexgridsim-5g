# flexgridsim-5g
Simulador de eventos discretos para Redes Ópticas Elásticas adaptado para a arquitetura de comunicação 5G.
Autor: Adriel Rodrigues

Recomenda-se usar Java IDE (preferência por Eclipse)

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
