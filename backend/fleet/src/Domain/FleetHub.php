<?php

namespace App\Domain;

use App\Domain\Model\Error\FleetMemberInvitationError;
use App\Domain\Model\Fleet;
use App\Domain\Port\FleetRepository;
use App\Domain\Service\FleetMemberInviter;
use Core\Domain\Value\Fleet\FleetId;
use Core\Domain\Value\User\UserEmail;
use Fp\Functional\Either\Either;

readonly class FleetHub
{
    public function __construct(
        private FleetRepository    $fleetRepository,
        private FleetMemberInviter $fleetMemberInviter,
    ) {}

    public function getFleet(FleetId $fleetId): ?Fleet
    {
        return $this->fleetRepository->findById($fleetId);
    }

    /**
     * @return  Either<FleetMemberInvitationError, null>
     */
    public function inviteMember(FleetId $fleetId, UserEmail $email): Either
    {
        return $this->fleetMemberInviter->inviteMember($fleetId, $email);
    }
}
