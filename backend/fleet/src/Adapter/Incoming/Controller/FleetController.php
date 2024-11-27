<?php

namespace App\Adapter\Incoming\Controller;

use App\Domain\FleetHub;
use Core\Domain\Value\Fleet\FleetId;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

class FleetController extends AbstractController
{
    public function __construct(
        private readonly FleetHub $fleetHub
    ) {}

    #[Route('/fleet/{fleetId}', name: 'get_fleet')]
    public function getFleet(string $fleetId): Response
    {
        $fleet = $this->fleetHub->getFleet(FleetId::of($fleetId));

        return match ($fleet)
        {
            null => $this->json(['error' => 'Fleet not found'], 404),
            default => $this->json($fleet),
        };
    }
}
