package com.example.almada;

import br.ufc.great.caos.api.offload.Offloadable;

public interface ICalc {

    @Offloadable
    public int soma(int a, int b);
}
