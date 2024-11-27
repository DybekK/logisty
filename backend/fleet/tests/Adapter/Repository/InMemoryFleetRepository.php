<?php

namespace App\Tests\Adapter\Repository;

use App\Domain\Model\Fleet;
use App\Domain\Port\FleetRepository;
use Core\Domain\Value\Fleet\FleetId;
use Fp\Collections\ArrayList;

class InMemoryFleetRepository implements FleetRepository
{
    public function __construct(
        private ArrayList $fleets = new ArrayList([])
    ) {}

    //TODO: remove when concrete is implemented
    public function create(Fleet $fleet): ?Fleet
    {
        $this->fleets = $this->fleets->appended($fleet);
        return $fleet;
    }

    public function findById(FleetId $fleetId): ?Fleet
    {
        return $this->fleets
            ->first(fn(Fleet $fleet) => $fleet->fleetId->eq($fleetId))
            ->get();
    }
}