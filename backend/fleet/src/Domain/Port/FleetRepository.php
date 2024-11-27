<?php

namespace App\Domain\Port;

use App\Domain\Model\Fleet;
use Core\Domain\Value\Fleet\FleetId;

interface FleetRepository
{
    public function findById(FleetId $fleetId): ?Fleet;
}
