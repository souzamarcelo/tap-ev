# TAP EV: Traffic Assignment Problem with Electric Vehicles

The Traffic Assignment Problem with Electric Vehicles (TAP-EV) considers that drivers of electric vehicles care about the travel time and energy consumption simultaneously. Therefore, the TAP-EV must address both objectives at the same time (a bi-objective problem). This repository contains the source code related to the TAP-EV, including:
+ Algorithms:
  + biobjective shortest path
  + biobjective successive averages (with greedy energy and greedy slope strategies)
+ Cost functions:
  + simple and detailed energy battery models
  + volume delay function
+ Some experiments

The following paper describes the proposed algorithms and the experimental results.

> **A bi-objective method of traffic assignment for electric vehicles**<br>
> Marcelo de Souza, Marcus Ritt, Ana L. C. Bazzan<br>
> IEEE 19th International Conference on Intelligent Transportation Systems (ITSC), Rio de Janeiro, p. 2319-2324, 2016<br>
> DOI: https://doi.org/10.1109/ITSC.2016.7795930

**Bibtex**

```bibtex
@inproceedings{SouzaEtAl2016tapev,
  title        = {A bi-objective method of traffic assignment for electric vehicles},
  author       = {Souza, Marcelo and Ritt, Marcus and Bazzan, Ana},
  booktitle    = {2016 IEEE 19th International Conference on Intelligent Transportation Systems (ITSC)},
  pages        = {2319--2324},
  year         = {2016},
  organization = {IEEE}
}
```

Please, make sure to reference us if you use our methods in your research.

***

## People

**Maintainer:** [Marcelo de Souza](https://souzamarcelo.github.io)

**Contributors:** [Marcus Ritt](https://www.inf.ufrgs.br/~mrpritt) and [Ana Bazzan](https://www.inf.ufrgs.br/~bazzan)

**Contact:** marcelo.desouza@udesc.br
