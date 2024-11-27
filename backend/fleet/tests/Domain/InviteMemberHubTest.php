<?php

namespace App\Tests\Domain;

use App\Domain\FleetHub;
use App\Domain\Model\Error\FleetMemberInvitationError;
use App\Domain\Model\Fleet;
use App\Domain\Model\User;
use App\Domain\Model\UserInvitation;
use App\Domain\Port\FleetRepository;
use App\Domain\Port\UserProvider;
use App\Domain\Service\FleetMemberInviter;
use App\Tests\Adapter\Http\FakeUserProvider;
use App\Tests\Adapter\Repository\InMemoryFleetRepository;
use Core\Domain\Value\Fleet\FleetId;
use Core\Domain\Value\Fleet\FleetName;
use Core\Domain\Value\User\UserEmail;
use Core\Domain\Value\User\UserId;
use Core\Domain\Value\User\UserInvitationId;
use DateTimeImmutable;
use PHPUnit\Framework\TestCase;

class InviteMemberHubTest extends TestCase
{
    private FleetHub $fleetHub;
    private FleetRepository $fleetRepository;
    private UserProvider $userProvider;

    protected function setUp(): void
    {
        $this->fleetRepository = new InMemoryFleetRepository();
        $this->userProvider = new FakeUserProvider();

        $this->fleetHub = new FleetHub(
            fleetRepository: $this->fleetRepository,
            fleetMemberInviter: new FleetMemberInviter($this->fleetRepository, $this->userProvider)
        );
    }

    public function testReturnsNoErrors(): void
    {
        // Arrange fleet
        $now = new DateTimeImmutable();
        $fleet = new Fleet(
            fleetId: FleetId::random(),
            fleetName: FleetName::of('TestFleet'),
            createdAt: $now,
            updatedAt: $now
        );

        // Arrange user
        $email = UserEmail::of('test@example.com');

        // Act
        $this->fleetRepository->create($fleet);

        $result = $this->fleetHub->inviteMember($fleet->fleetId, $email);

        // Assert
        self::assertTrue($result->isRight());
    }

    public function testReturnsErrorIfFleetNotExists(): void
    {
        // Arrange
        $fleetId = FleetId::random();
        $email = UserEmail::of('test@example.com');

        // Act
        $result = $this->fleetHub->inviteMember($fleetId, $email);

        // Assert
        self::assertEquals(FleetMemberInvitationError::fleetNotExists(), $result);
    }

    public function testReturnsErrorIfUserAlreadyExists(): void
    {
        // Arrange fleet
        $now = new DateTimeImmutable();
        $fleet = new Fleet(
            fleetId: FleetId::random(),
            fleetName: FleetName::of('TestFleet'),
            createdAt: $now,
            updatedAt: $now
        );

        // Arrange user
        $user = new User(
            userId: UserId::random(),
            email: UserEmail::of('test@example.com')
        );

        // Act
        $this->fleetRepository->create($fleet);
        $this->userProvider->createUser($user);

        $result = $this->fleetHub->inviteMember($fleet->fleetId, $user->email);

        // Assert
        self::assertEquals(FleetMemberInvitationError::userAlreadyExists(), $result);
    }

    public function testReturnsErrorIfInvitationAlreadySent(): void
    {
        // Arrange fleet
        $now = new DateTimeImmutable();
        $fleet = new Fleet(
            fleetId: FleetId::random(),
            fleetName: FleetName::of('TestFleet'),
            createdAt: $now,
            updatedAt: $now
        );

        // Arrange user
        $email = UserEmail::of('test@example.com');

        // Arrange invitation
        $invitation = new UserInvitation(
            invitationId: UserInvitationId::random(),
            fleetId: $fleet->fleetId,
            email: $email,
            dueAt: $now,
            createdAt: $now
        );

        // Act
        $this->fleetRepository->create($fleet);
        $this->userProvider->createInvitation($invitation);

        $result = $this->fleetHub->inviteMember($fleet->fleetId, $email);

        // Assert
        self::assertEquals(FleetMemberInvitationError::userAlreadyInvited(), $result);
    }
}