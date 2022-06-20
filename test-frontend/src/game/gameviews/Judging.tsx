import Players, { PlayerList } from '../players/Players';

export const JudgingView = (props: { players: PlayerList }) => {
	return <Players players={props.players} />;
};
