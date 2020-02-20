package com.example.unitally.app_modules;

import com.example.unitally.objects.Unit;

import java.util.List;

public interface NextTierCallback {
    void OnNextTierReached(List<Unit> NextTierList, List<Unit> PreviousTierList);
}
