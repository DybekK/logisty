<?php

namespace App\Domain\Port;

use App\Domain\Model\User;
use App\Domain\Model\UserInvitation;
use Core\Domain\Value\User\UserEmail;

interface UserProvider
{
    public function getUserByEmail(UserEmail $email): ?User;

    public function getUserInvitationByEmail(UserEmail $email): ?UserInvitation;
}
