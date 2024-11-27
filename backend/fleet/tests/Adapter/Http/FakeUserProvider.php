<?php

namespace App\Tests\Adapter\Http;

use App\Domain\Model\User;
use App\Domain\Model\UserInvitation;
use App\Domain\Port\UserProvider;
use Core\Domain\Value\User\UserEmail;
use Fp\Collections\ArrayList;

class FakeUserProvider implements UserProvider
{
    public function __construct(
        private ArrayList $users = new ArrayList([]),
        private ArrayList $invitations = new ArrayList([])
    ) {}

    //TODO: remove when concrete is implemented
    public function createUser(User $user): void
    {
        $this->users = $this->users->appended($user);
    }

    //TODO: remove when concrete is implemented
    public function createInvitation(UserInvitation $invitation): void
    {
        $this->invitations = $this->invitations->appended($invitation);
    }

    public function getUserByEmail(UserEmail $email): ?User
    {
        return $this->users
            ->first(fn(User $user) => $user->email->eq($email))
            ->get();
    }

    public function getUserInvitationByEmail(UserEmail $email): ?UserInvitation
    {
        return $this->invitations
            ->first(fn(UserInvitation $invitation) => $invitation->email->eq($email))
            ->get();
    }
}