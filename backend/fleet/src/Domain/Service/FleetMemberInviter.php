<?php

namespace App\Domain\Service;

use App\Domain\Model\Error\FleetMemberInvitationError;
use App\Domain\Model\UserInvitation;
use App\Domain\Port\FleetRepository;
use App\Domain\Port\UserProvider;
use Core\Domain\Value\Fleet\FleetId;
use Core\Domain\Value\User\UserEmail;
use Fp\Functional\Either\Either;
use Fp\Functional\Option\Option;

readonly class FleetMemberInviter
{
    public function __construct(
        private FleetRepository $fleetRepository,
        private UserProvider    $userProvider
    ) {}

    /**
     * @return Either<FleetMemberInvitationError, null>
     */
    public function inviteMember(FleetId $fleetId, UserEmail $email): Either
    {
        return $this->validateFleet($fleetId)
            ->flatMap(fn() => $this->validateUser($email))
            ->flatMap(fn() => $this->validateUserInvitation($email));
    }

    /**
     * @return Either<FleetMemberInvitationError, null>
     */
    private function validateFleet(FleetId $fleetId): Either
    {
        $fleet = $this->fleetRepository->findById($fleetId);

        return Option::fromNullable($fleet)
            ->map(fn() => Either::right(null))
            ->getOrElse(FleetMemberInvitationError::fleetNotExists());
    }

    /**
     * @return Either<FleetMemberInvitationError, null>
     */
    private function validateUser(UserEmail $email): Either
    {
        $user = $this->userProvider->getUserByEmail($email);

        return Option::fromNullable($user)
            ->map(fn() => FleetMemberInvitationError::userAlreadyExists())
            ->getOrElse(Either::right(null));
    }

    /**
     * @return Either<FleetMemberInvitationError, null>
     */
    private function validateUserInvitation(UserEmail $email): Either
    {
        $invitation = $this->userProvider->getUserInvitationByEmail($email);

        return Option::fromNullable($invitation)
            ->filter(fn(UserInvitation $invitation) => $invitation->acceptedAt === null)
            ->map(fn() => FleetMemberInvitationError::userAlreadyInvited())
            ->getOrElse(Either::right(null));
    }
}
